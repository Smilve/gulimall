package com.lvboaa.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

/**
 * SpringSession核心原理
 * 1）、@EnableRedisHttpSession导入RedisHttpSessionConfiguration配置
 *      1、给容器中添加了一个组件
 *          SessionRepository->RedisOperationsSessionRepository：Redis操作session，session的增删改查封装类
 *      2、SpringHttpSessionConfiguration->SessionRepositoryFilter->就是一个Filter：每个请求都会经过这个过滤器
 *          doFilterInternal方法->使用装饰者模式包装原始的request和response：SessionRepositoryRequestWrapper，SessionRepositoryResponseWrapper
 *          然后执行doFilter->通过该链
 *          以后获取session，request.getSession(),就是SessionRepositoryRequestWrapper的getSession方法->使用SessionRepository执行对应的增删改查
 *          也就是使用Redisson执行
 *自动续签延期，也有过期时间
 */

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.lvboaa.authserver.feign")
@EnableRedisHttpSession // 整合redis作为session存储
public class GulimallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallAuthServerApplication.class, args);
    }

}
