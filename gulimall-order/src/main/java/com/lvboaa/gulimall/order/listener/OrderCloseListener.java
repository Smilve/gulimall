package com.lvboaa.gulimall.order.listener;

import com.lvboaa.gulimall.order.entity.OrderEntity;
import com.lvboaa.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.impl.AMQImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/10/28 14:46
 */

@RabbitListener(queues = "order.release.order.queue")
@Service
@Slf4j
public class OrderCloseListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void releaseOrder(OrderEntity orderEntity, Message message, Channel channel) throws IOException {
        log.info("收到过期的订单，关闭订单："+orderEntity.getId());
        try {
            orderService.closeOrder(orderEntity);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
