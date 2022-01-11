package com.lvboaa.gulimall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/9/23 10:33
 */
@Configuration
// @EnableConfigurationProperties(ThreadPoolConfigProperties.class) //这个和Properties的Component写一个即可
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties pool){
        return new ThreadPoolExecutor(pool.getCoreSize(),pool.getMaxSize(),pool.getKeepAliveTime(), TimeUnit.SECONDS,new LinkedBlockingDeque<>(1000),
                Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
    }
}
