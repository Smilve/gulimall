package com.lvboaa.gulimall.config;

import com.lvboaa.gulimall.interceptor.CartInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/9/28 10:31
 */
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {

    @Autowired
    CartInterceptor cartInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cartInterceptor)
                .addPathPatterns("/**")
                ;
    }
}
