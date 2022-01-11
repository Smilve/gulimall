package com.lvboaa.gulimall.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.lvboaa.common.to.mq.SeckillOrderTo;
import com.lvboaa.common.utils.R;
import com.lvboaa.common.vo.MemberResponseVo;
import com.lvboaa.gulimall.seckill.feign.CouponFeignService;
import com.lvboaa.gulimall.seckill.feign.ProductFeignService;
import com.lvboaa.gulimall.seckill.interceptor.LoginUserInterceptor;
import com.lvboaa.gulimall.seckill.service.SecKillService;
import com.lvboaa.gulimall.seckill.to.SeckillSkuRedisTo;
import com.lvboaa.gulimall.seckill.vo.SeckillSessionWithSkusVo;
import com.lvboaa.gulimall.seckill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/11/5 13:59
 */
@Service
@Slf4j
public class SecKillServiceImpl implements SecKillService {

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String SESSION_CACHE_PREFIX = "seckill:sessions:";

    private final String SECKILL_CHARE_PREFIX = "seckill:skus";

    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";    //+商品随机码

    @Override
    public void uploadSeckillSkuLatest3Days() {
        // 上架秒杀商品信息：库存预热
        // 需要先锁定库存，秒杀结束如果还有商品就应该解锁对应的库存
        // todo 需要对保存的信息设置过期时间，在秒杀结束时过期
        // todo 秒杀后续流程，创建订单时简化了操作，如地址、运费等的计算；发送订单信息到死信队列中
        R r = couponFeignService.getLatest3DaysSession();
        if (r.getCode() == 0){
            List<SeckillSessionWithSkusVo> session =r.getData("data",new TypeReference<List<SeckillSessionWithSkusVo>>(){});
            if (session != null && session.size() > 0 ){
                //缓存到Redis
                //1、缓存活动信息
                saveSessionInfos(session);

                //2、缓存活动的关联商品信息
                saveSessionSkuInfo(session);
            }
        }
    }

    @SentinelResource(value = "getCurrentSeckillSkusResource",blockHandler = "blockHandler")
    @Override
    public List<SeckillSkuRedisTo> getCurrentSecKillSkus() {
        try (Entry entry = SphU.entry("seckillSkus")) {
            long now = new Date().getTime();
            Set<String> keys = redisTemplate.keys(SESSION_CACHE_PREFIX + "*");
            List<SeckillSkuRedisTo> collect= new ArrayList<>();
            BoundHashOperations<String, String, String > hashOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
            for (String key:keys){
                String replace = key.replace(SESSION_CACHE_PREFIX, "");
                String[] split = replace.split("_");
                long start = Long.parseLong(split[0]);
                long end = Long.parseLong(split[1]);
                if (now >= start && now <= end){
                    // 1_1
                    List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                    assert range != null;
                    // 对应的 sku秒杀信息
                    List<String> multiGet = hashOps.multiGet(range);
                    if (multiGet != null && multiGet.size() >0){
                        List<SeckillSkuRedisTo> collect1 = multiGet.stream().map(item -> {
                            SeckillSkuRedisTo redisTo = JSON.parseObject(item, SeckillSkuRedisTo.class);
                            // redisTo.setRandomCode(null); //秒杀开始的时候需要给随机码 预告随机不需要带
                            return redisTo;
                        }).collect(Collectors.toList());
                        collect.addAll(collect1);
                    }
                }
            }
            return collect;
        }catch (BlockException e){
            log.error("资源被限流{}",e.getMessage());
        }
        return null;
    }

    public List<SeckillSkuRedisTo> blockHandler(BlockException e) {

        log.error("getCurrentSeckillSkusResource被限流了,{}",e.getMessage());
        return null;
    }

    @Override
    public SeckillSkuRedisTo getSkuSeckilInfo(Long skuId) {
        // 找到所有需要参与秒杀的商品的key
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
        Set<String> keys = hashOps.keys();
        if (keys!=null && keys.size()>0){
            // 1_1
            String regx = "\\d_"+skuId;
            for (String key:keys){
                if (Pattern.matches(regx,key)){
                    String s = hashOps.get(key);
                    SeckillSkuRedisTo redisTo = JSON.parseObject(s, SeckillSkuRedisTo.class);
                    //随机码
                    Long currentTime = System.currentTimeMillis();
                    Long startTime = redisTo.getStartTime();
                    Long endTime = redisTo.getEndTime();
                    //如果当前时间大于等于秒杀活动开始时间并且要小于活动结束时间
                    if (currentTime >= startTime && currentTime <= endTime) {
                        return redisTo;
                    }
                    // 正在预售，把随机码设为null
                    redisTo.setRandomCode(null);
                    return redisTo;
                }
            }

        }
        return null;
    }

    @Override
    public String secKill(String killId, String key, Integer num) {
        MemberResponseVo loginUser = LoginUserInterceptor.loginUser.get();
        // 1.获取当前秒杀商品的详细信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
        String redisTo = hashOps.get(killId);
        if (!StringUtils.isEmpty(redisTo)){
            SeckillSkuRedisTo skuRedisTo = JSON.parseObject(redisTo, SeckillSkuRedisTo.class);
            // 合法性校验
            Long startTime = skuRedisTo.getStartTime();
            Long endTime = skuRedisTo.getEndTime();
            Long nowTime = System.currentTimeMillis();
            // 校验时间合法性
            if (nowTime >= startTime && nowTime<=endTime){
                // 校验商品id和随机码
                String skuId = skuRedisTo.getPromotionSessionId()+"_"+skuRedisTo.getSkuId();
                if (skuRedisTo.getRandomCode().equals(key) && killId.equals(skuId)){
                    // 验证每个人的购买上限
                    if (num <= skuRedisTo.getSeckillLimit()){
                        // 验证这个人是否已经买过了，幂等性  userId_sessionId_skuId 标致是否买过
                        //SETNX
                        String redisKey = loginUser.getId()+"_"+skuId;
                        // 类似分布式锁，一次只能占一个；自动过期，活动过期就锁就失效
                        long ttl = endTime - nowTime;
                        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(),ttl, TimeUnit.MILLISECONDS);
                        if (ifAbsent){
                            // 占位成功表示从来么买过，就可以获取信号量了
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + skuRedisTo.getRandomCode());
                            boolean acquire = semaphore.tryAcquire(num);// 从信号量中取出num个
                            if (acquire){
                                // 秒杀成功
                                // 快速下单，发送MQ消息
                                String timeId = IdWorker.getTimeId();
                                SeckillOrderTo orderTo = new SeckillOrderTo();
                                orderTo.setOrderSn(timeId);
                                orderTo.setPromotionSessionId(skuRedisTo.getPromotionSessionId());
                                orderTo.setSkuId(skuRedisTo.getSkuId());
                                orderTo.setSeckillPrice(skuRedisTo.getSeckillPrice());
                                orderTo.setNum(num);
                                orderTo.setMemberId(loginUser.getId());
                                rabbitTemplate.convertAndSend("order-event-exchange","order.seckill.order",orderTo);
                                log.info("耗时:"+(System.currentTimeMillis()-nowTime));
                                return timeId;
                            }
                        }
                    }
                }
            }
        }
        // 失败
        return null;
    }

    /**
     * 缓存秒杀活动
     * @param session
     */
    private void saveSessionInfos(List<SeckillSessionWithSkusVo> session) {
        session.stream().forEach(s->{
            long startTime = s.getStartTime().getTime();
            long endTime = s.getEndTime().getTime();
            String key = SESSION_CACHE_PREFIX+startTime+"_"+endTime;
            // 如果没有key才存，幂等性处理
            if (!redisTemplate.hasKey(key)){
                List<String> collect = s.getRelationSkus().stream()
                        .map(r -> r.getPromotionSessionId() + "_" + r.getSkuId().toString())
                        .collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key,collect);
                redisTemplate.expire(key,endTime-System.currentTimeMillis(),TimeUnit.MILLISECONDS);
            }
        });
    }

    /**
     * 缓存秒杀活动对应的商品信息
     * @param session
     */
    private void saveSessionSkuInfo(List<SeckillSessionWithSkusVo> session) {
        session.stream().forEach(s->{
            //准备hash操作，绑定hash
            BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
            s.getRelationSkus().stream().forEach(seckillSkuVo -> {
                //生成随机码
                String token = UUID.randomUUID().toString().replace("-", "");
                String redisKey = seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString();
                // 幂等性处理，不要重复上架
                if (!operations.hasKey(redisKey)) {

                    //缓存我们商品信息
                    SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                    Long skuId = seckillSkuVo.getSkuId();
                    //1、先查询sku的基本信息，调用远程服务
                    R info = productFeignService.getSkuInfo(skuId);
                    if (info.getCode() == 0) {
                        SkuInfoVo skuInfo = info.getData("skuInfo",new TypeReference<SkuInfoVo>(){});
                        redisTo.setSkuInfo(skuInfo);
                    }

                    //2、sku的秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo,redisTo);

                    //3、设置当前商品的秒杀时间信息
                    redisTo.setStartTime(s.getStartTime().getTime());
                    redisTo.setEndTime(s.getEndTime().getTime());

                    //4、设置商品的随机码（防止恶意攻击，如使用软件抢秒杀商品，在减库存的时候也可以验证随机码）
                    redisTo.setRandomCode(token);

                    //序列化json格式存入Redis中
                    String seckillValue = JSON.toJSONString(redisTo);
                    operations.put(redisKey,seckillValue);

                    //如果当前这个场次的商品库存信息已经上架就不需要上架
                    //5、使用库存作为分布式Redisson信号量（限流，可以很快处理请求，适用于分布式大量请求）
                    // 使用库存作为分布式信号量
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    // 商品可以秒杀的数量作为信号量，在秒杀成功扣库存的时候使用
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount()); // 设置信号量的值
                    semaphore.expire(s.getEndTime().getTime()-System.currentTimeMillis(),TimeUnit.MILLISECONDS);
                }
            });
        });
    }

}
