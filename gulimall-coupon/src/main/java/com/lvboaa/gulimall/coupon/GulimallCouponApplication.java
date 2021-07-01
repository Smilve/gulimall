package com.lvboaa.gulimall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 *  如何使用配置中心进行配置管理
 *      导入依赖
 *      新建bootstrap.properties，配置项目名和nacos地址
 *      在nacos上新建gulimall-coupon.properties文件并输入内容，默认是这个 可以配置
 *      在调用nacos的属性的文件上添加 @RefreshScope 注解，动态属性配置文件
 *      配置中心的配置优先级优于本地配置
 *
 */
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.lvboaa.gulimall.coupon.dao")
public class  GulimallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallCouponApplication.class, args);
    }

}
