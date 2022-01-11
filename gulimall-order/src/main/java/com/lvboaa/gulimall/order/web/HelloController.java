package com.lvboaa.gulimall.order.web;

import com.lvboaa.gulimall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/10/8 15:01
 */

@Controller
public class HelloController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RequestMapping("/creatMessage")
    @ResponseBody
    public String testSendMessage(){
        OrderEntity entity = new OrderEntity();
        entity.setId(123l);
        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",entity, new CorrelationData(UUID.randomUUID().toString()));
        return "success";
    }

    @GetMapping("/{page}.html")
    public String listPage(@PathVariable("page") String page){

        return page;
    }
}
