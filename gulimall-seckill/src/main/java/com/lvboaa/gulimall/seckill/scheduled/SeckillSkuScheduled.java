package com.lvboaa.gulimall.seckill.scheduled;

import com.lvboaa.gulimall.seckill.service.SecKillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 *  秒杀商品的定时上架
 *      每天晚上3点执行：上架最近三天需要秒杀的商品(服务被访问不会是高峰期，服务器资源大量被闲置)
 *      当天：00:00:00 - 23:59:59
 *      明天: 00:00:00 - 23:59:59
 *      后天: 00:00:00 - 23:59:59
 *
 * @author lv.bo
 * @date 2021/11/5 13:54
 */
@Slf4j
@Service
public class SeckillSkuScheduled {

    @Autowired
    SecKillService secKillService;

    @Autowired
    RedissonClient redissonClient;

    //秒杀商品上架功能的锁
    private final String upload_lock = "seckill:upload:lock";


    // 秒	分	时	日	月	周
    @Scheduled(cron = "*/20 * * * * ?")
    public void uploadSecKillSkuLatest3Days(){
        // 如果启动了多台机器，只需要一台机器即可，也就是加个分布式锁  保证原子性
        RLock lock = redissonClient.getLock(upload_lock);
        try{
            lock.lock(10, TimeUnit.SECONDS);
            log.info("上架秒杀的商品信息");
            secKillService.uploadSeckillSkuLatest3Days();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

    }
}
