package com.lvboaa.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvboaa.common.utils.PageUtils;
import com.lvboaa.gulimall.order.entity.OrderEntity;
import com.lvboaa.gulimall.order.vo.OrderConfirmVo;
import com.lvboaa.gulimall.order.vo.OrderSubmitVo;
import com.lvboaa.gulimall.order.vo.SubmitResponseVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author lvbo
 * @email 2484420621@qq.com
 * @date 2021-07-01 00:58:07
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 给订单确认页返回需要用的数据
     * @return
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitResponseVo submitOrder(OrderSubmitVo submitVo);
}

