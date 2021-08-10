package com.lvboaa.gulimall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @author: lvbo
 * @date: 2021/7/6 10:09
 * @description:
 */
@Configuration
public class GulimallCorsConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();

        // 配置跨域
        // 任意请求头
        configuration.addAllowedHeader("*");
        // 任意方法
        configuration.addAllowedMethod("*");
        // 任意请求来源
        configuration.addAllowedOrigin("*");
        // 允许携带cookie跨域
        configuration.setAllowCredentials(true);

        source.registerCorsConfiguration("/**",configuration);
        return new CorsWebFilter(source);
    }
}
