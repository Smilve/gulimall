package com.lvboaa.gulimall.ware.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/10/27 18:28
 */
@Configuration
public class RabbitMQConfig {
    /**
     * 使用JSON序列化机制，进行消息转换
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

//     @RabbitListener(queues = "stock.release.stock.queue")
//     public void handle(Message message) {
//
//     }

    /**
     * 库存服务默认的交换机
     * @return
     */
    @Bean
    public Exchange stockEventExchange(){
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return new TopicExchange("stock-event-exchange",true,false);
    }

    /**
     * 普通队列
     * @return
     */
    @Bean
    public Queue stockReleaseStockQueue() {
        //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        return new Queue("stock.release.stock.queue", true, false, false);
    }


    /**
     * 延迟队列
     * @return
     */
    @Bean
    public Queue stockDelay() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange","stock-event-exchange");
        map.put("x-dead-letter-routing-key","stock.release");
        // 消息过期时间 15秒(测试)
        map.put("x-message-ttl",350000);


        Queue queue = new Queue("stock.delay.queue", true, false, false,map);
        return queue;
    }


    /**
     * 交换机与普通队列绑定
     * @return
     */
    @Bean
    public Binding stockLocked() {
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        // 			Map<String, Object> arguments
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release.#",
                null);
    }


    /**
     * 交换机与延迟队列绑定
     * @return
     */
    @Bean
    public Binding stockLockedBinding() {
        return new Binding("stock.delay.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.locked",
                null);
    }

}
