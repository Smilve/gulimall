package com.lvboaa.gulimall.order.controller;

import com.lvboaa.gulimall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/10/8 11:14
 */
@RestController
public class RabbitController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMq")
    public String sendMq(){
        for (int i=0;i<10;i++){
            OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
            orderReturnReasonEntity.setName("name："+i);
            rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",orderReturnReasonEntity,new CorrelationData(UUID.randomUUID().toString()));
        }
        return "ok";
    }
}
