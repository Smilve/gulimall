package com.lvboaa.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.lvboaa.common.exception.NoStockException;
import com.lvboaa.common.to.OrderTo;
import com.lvboaa.common.to.SkuHasStockVo;
import com.lvboaa.common.to.mq.StockDetailTo;
import com.lvboaa.common.to.mq.StockLockedTo;
import com.lvboaa.common.utils.R;
import com.lvboaa.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.lvboaa.gulimall.ware.entity.WareOrderTaskEntity;
import com.lvboaa.gulimall.ware.feign.OrderFeignService;
import com.lvboaa.gulimall.ware.service.WareOrderTaskDetailService;
import com.lvboaa.gulimall.ware.service.WareOrderTaskService;
import com.lvboaa.gulimall.ware.vo.LockStockResult;
import com.lvboaa.gulimall.ware.vo.OrderItemVo;
import com.lvboaa.gulimall.ware.vo.OrderVo;
import com.lvboaa.gulimall.ware.vo.WareSkuLockVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvboaa.common.utils.PageUtils;
import com.lvboaa.common.utils.Query;

import com.lvboaa.gulimall.ware.dao.WareSkuDao;
import com.lvboaa.gulimall.ware.entity.WareSkuEntity;
import com.lvboaa.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import sun.plugin2.message.Message;

@Service("wareSkuService")
@Slf4j
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {



    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    OrderFeignService orderFeignService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> skuHasStockVos = skuIds.stream().map(item -> {
            Long count = this.baseMapper.getSkuStock(item);
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            skuHasStockVo.setSkuId(item);
            skuHasStockVo.setHasStock(count == null?false:count > 0);
            return skuHasStockVo;
        }).collect(Collectors.toList());
        return skuHasStockVos;
    }

    /**
     * 为某个订单锁定库存
     *
     * rollbackFor = NoStockException.class
     * 默认只要是运行时异常都会回滚
     *
     *  库存解锁的场景
     *  1、下订单成功，订单过期没有支付被系统自动取消或被用户手动取消
     *  2、下订单成功，锁库存成功，接下来的业务调用失败，导致订单回滚，之前锁定的库存就要自动解锁
     * @param lockVo
     * @return
     */
    @Transactional(rollbackFor = NoStockException.class)
    @Override
    public void orderLockStock(WareSkuLockVo lockVo) {
        /**
         * 保存库存工作单详情信息
         * 追溯
         */
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(lockVo.getOrderSn());
        wareOrderTaskEntity.setCreateTime(new Date());
        wareOrderTaskService.save(wareOrderTaskEntity);


        // 按照下单的收货地址，找到一个就近仓库，锁定库存
        // 1.找到每个商品在哪个仓库有库存
        List<OrderItemVo> locks = lockVo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            // 查询这个商品在哪有库存
            List<Long> wareIds = this.baseMapper.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());

        for (SkuWareHasStock stock:collect){
            Boolean skuStocked = false;
            Long skuId = stock.getSkuId();
            List<Long> wareIds = stock.getWareId();
            if (wareIds==null || wareIds.size() == 0){
                // 没有任何仓库有库存
               throw new NoStockException(skuId);
            }
            // 1、如果商品锁定成功，将当前商品锁定了几件的工作单记录发给MQ
            // 2、只要有一个锁定失败。前面保存的消息就回滚了
            for (Long wareId:wareIds){
                // 成功返回1，失败返回0
                Long count = this.baseMapper.lockSkuStock(skuId, wareId, stock.getNum());
                if (count == 1){
                    skuStocked = true;
                    WareOrderTaskDetailEntity taskDetailEntity = WareOrderTaskDetailEntity.builder()
                            .skuId(skuId)
                            .skuName("")
                            .skuNum(stock.getNum())
                            .taskId(wareOrderTaskEntity.getId())
                            .wareId(wareId)
                            .lockStatus(1)
                            .build();
                    wareOrderTaskDetailService.save(taskDetailEntity);

                    //TODO 告诉MQ库存锁定成功
                    StockLockedTo lockedTo = new StockLockedTo();
                    lockedTo.setId(wareOrderTaskEntity.getId());
                    StockDetailTo detailTo = new StockDetailTo();
                    BeanUtils.copyProperties(taskDetailEntity,detailTo);
                    // 本来可以只发id的，防止回滚以后找不到数据
                    lockedTo.setDetailTo(detailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",lockedTo);
                    log.info("告诉MQ锁定库存消息");
                    break;
                }
            }

            if (!skuStocked){
                // 当前商品没有锁住
                throw new NoStockException(skuId);
            }
        }
        // 只要没抛异常，肯定是全部锁成功的
    }

    @Override
    public void unlockStock(StockLockedTo to) {
        //库存工作单的id
        StockDetailTo detail = to.getDetailTo();
        Long detailId = detail.getId();

        /**
         * 解锁
         * 1、查询数据库关于这个订单锁定库存信息
         *   有：证明库存锁定成功了
         *      解锁：订单状况
         *          1、没有这个订单，必须解锁库存
         *          2、有这个订单，不一定解锁库存
         *              订单状态：已取消：解锁库存
         *                      已支付(已完成)：不能解锁库存
         *      锁定库存细节状态为已解锁也不需要解锁了，不需要进行下面的判断
         *   没有：库存锁定失败了，库存回滚了，这种情况无需解锁
         *
         */
        WareOrderTaskDetailEntity taskDetailInfo = wareOrderTaskDetailService.getById(detailId);
        if (taskDetailInfo != null && taskDetailInfo.getLockStatus() != 2) {
            //查出wms_ware_order_task工作单的信息
            Long id = to.getId();
            WareOrderTaskEntity orderTaskInfo = wareOrderTaskService.getById(id);
            //获取订单号查询订单状态
            String orderSn = orderTaskInfo.getOrderSn();
            //远程查询订单信息
            R orderData = orderFeignService.getOrderStatus(orderSn);
            if (orderData.getCode() == 0) {
                //订单数据返回成功
                OrderVo orderInfo = orderData.getData("data", new TypeReference<OrderVo>() {});

                log.info("订单："+orderInfo.toString());
                //判断订单状态是否已取消或者支付或者订单不存在
                if (orderInfo == null || orderInfo.getStatus() == 4) {
                    //订单已被取消并且库存已锁定，才能解锁库存 OrderStatusEnum
                    if (taskDetailInfo.getLockStatus() == 1) {
                        //当前库存工作单详情状态1，已锁定，但是未解锁才可以解锁
                        unLockStock(detail.getSkuId(),detail.getWareId(),detail.getSkuNum(),detailId);
                    }
                }
            } else {
                //消息拒绝以后重新放在队列里面，让别人继续消费解锁
                //远程调用服务失败
                throw new RuntimeException("远程调用服务失败");
            }
        } else {
            //无需解锁
        }
    }

    /**
     * 防止订单服务卡顿，导致订单状态消息一直改不了，库存优先到期，查订单状态新建，什么都不处理
     * 导致卡顿的订单，永远都不能解锁库存
     * @param orderTo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unlockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        //查一下最新的库存解锁状态，防止重复解锁库存
        WareOrderTaskEntity orderTaskEntity = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);

        //按照工作单的id找到所有 没有解锁的库存，进行解锁
        Long id = orderTaskEntity.getId();
        List<WareOrderTaskDetailEntity> list = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", id).eq("lock_status", 1));

        for (WareOrderTaskDetailEntity taskDetailEntity : list) {
            unLockStock(taskDetailEntity.getSkuId(),
                    taskDetailEntity.getWareId(),
                    taskDetailEntity.getSkuNum(),
                    taskDetailEntity.getId());
        }
    }

    /**
     * 解锁库存的方法
     * @param skuId
     * @param wareId
     * @param num
     * @param taskDetailId
     */
    public void unLockStock(Long skuId,Long wareId,Integer num,Long taskDetailId) {

        //库存解锁
        this.baseMapper.unLockStock(skuId,wareId,num);

        //更新工作单的状态
        WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity();
        taskDetailEntity.setId(taskDetailId);
        //变为已解锁
        taskDetailEntity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(taskDetailEntity);

    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }

}