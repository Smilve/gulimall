package com.lvboaa.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/9/24 17:19
 */
@Configuration
public class GulimallSessionConfig {
    @Bean //作用域：当前域(解决子域session共享问题)
    public CookieSerializer cookieSerializer() {

        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();

        //放大作用域，使cookie在主域上也能生效  比如说 auth.gulimall.com存的session可以在 gulimall.com上使用
        // 但是不能在别的域名下使用，如 atguigu.com(系统在访问这个域名的时候不会带上session)  所以要使用单点登录
         cookieSerializer.setDomainName("gulimall.com");
        // 修改cookie的名字，默认jsessionid
        cookieSerializer.setCookieName("GULISESSION");

        return cookieSerializer;
    }

    @Bean //使用json序列化将session存入redis中
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
