package com.lvboaa.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 *  整合mybatis-plus
 *      导入依赖
 *      配置数据源
 *          导入数据库驱动
 *          在application.yml中配置数据源信息
 *      配置mybatis-plus
 *          @MapperScan
 *          配置dao层映射xml文件
 *
 */
@EnableFeignClients(basePackages = "com.lvboaa.gulimall.product.feign")
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.lvboaa.gulimall.product.dao")
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
