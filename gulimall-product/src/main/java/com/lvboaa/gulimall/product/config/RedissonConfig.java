package com.lvboaa.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/9/10 17:58
 */
@Configuration
public class RedissonConfig {

    /**
     * 所有对Redisson的使用都是通过这个对象
     *
     */
    @Bean(destroyMethod="shutdown")
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://47.98.137.243:6379");
        return Redisson.create(config);
    }

}