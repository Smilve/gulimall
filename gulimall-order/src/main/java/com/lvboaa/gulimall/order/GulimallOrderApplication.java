package com.lvboaa.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * 使用RabbitMQ
 * 1.引入amqp场景，RabbitAutoConfiguration自动生效
 * 2.给容器中自动配置了
 *      RabbitTemplate、AmqpAdmin、CachingConnectionFactory、RabbitMessagingTemplate
 * 3.@EnableRabbit 开启RabbitMQ的功能
 * 4.@RabbitListener  监听队列消息，得到消息(队列必须存在)；必须开启@EnableRabbit
 *
 */
@EnableRedisHttpSession
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.lvboaa.gulimall.order.dao")
@EnableRabbit
@EnableFeignClients
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
