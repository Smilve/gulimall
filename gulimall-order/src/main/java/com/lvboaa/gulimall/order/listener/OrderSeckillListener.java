package com.lvboaa.gulimall.order.listener;

import com.lvboaa.common.to.mq.SeckillOrderTo;
import com.lvboaa.gulimall.order.entity.OrderEntity;
import com.lvboaa.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/11/11 11:38
 */

@RabbitListener(queues = "order.seckill.order.queue")
@Component
@Slf4j
public class OrderSeckillListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void releaseOrder(SeckillOrderTo seckillOrderTo, Message message, Channel channel) throws IOException {
        log.info("收到秒杀的订单："+seckillOrderTo.toString());
        try {
            orderService.createSeckillOrder(seckillOrderTo);
            // 在关闭订单的时候手动调用支付宝收单(防止时延问题->订单都关闭了，异步回调才来)
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            // 失败的时候是否重新加入队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
