package com.lvboaa.gulimall.order.web;

import com.lvboaa.gulimall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @ResponseBody
    @GetMapping("/order/p1")
    @PreAuthorize("hasAuthority('p1')")
    public String getOrder(){
        return "p1";
    }

    @ResponseBody
    @GetMapping("/order/p2")
    @PreAuthorize("hasAuthority('p2')")
    public String getOrder2(){
        return "p2";
    }

    @ResponseBody
    @GetMapping("/order/p3")
    @PreAuthorize("hasAuthority('p3')")
    public String getOrder3(){
        return "p3";
    }
}
