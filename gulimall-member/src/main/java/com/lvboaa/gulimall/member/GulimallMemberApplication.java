package com.lvboaa.gulimall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 *  想要远程调用服务
 *      引入open-feign
 *      编写一个接口，告诉SpringCloud 这个接口需要调用远程服务
 *      声明接口的每一个方法是调用哪个远程服务的哪个请求
 *  开启远程调用功能 @EnableFeignClients
 */
@EnableFeignClients(basePackages = "com.lvboaa.gulimall.member.feign")
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.lvboaa.gulimall.member.dao")
@EnableRedisHttpSession
public class GulimallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallMemberApplication.class, args);
    }

}
