//package com.lvboaa.gulimall.gateway.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//
//@Configuration
//public class ResouceServerConfig {
//    public static final String RESOURCE_ID = "resource1";
//
//    /**
//     * 统一认证服务(auth-server) 资源拦截
//     */
//    @Configuration
//    @EnableResourceServer
//    public class OAuth2ServerConfig extends
//            ResourceServerConfigurerAdapter {
//        @Autowired
//        private TokenStore tokenStore;
//
//        @Override
//        public void configure(ResourceServerSecurityConfigurer resources) {
//            resources.tokenStore(tokenStore).resourceId(RESOURCE_ID)
//                    .stateless(true);
//        }
//
//        // 代表通过网关访问oauth2的都直接通过不需要授权
//        @Override
//        public void configure(HttpSecurity http) throws Exception {
//            http.authorizeRequests()
//                    .antMatchers("/oauth2/**").permitAll();
//        }
//    }
//
//    /**
//     * 订单服务：访问订单服务的需要 order的scope
//     */
//    @Configuration
//    @EnableResourceServer
//    public class OrderServerConfig extends
//            ResourceServerConfigurerAdapter {
//        @Autowired
//        private TokenStore tokenStore;
//        @Override
//        public void configure(ResourceServerSecurityConfigurer resources) {
//            resources.tokenStore(tokenStore).resourceId(RESOURCE_ID)
//                    .stateless(true);
//        }
//        @Override
//        public void configure(HttpSecurity http) throws Exception {
//            http
//                    .authorizeRequests()
//                    .antMatchers("/order/**").access("#oauth2.hasScope('scope1')");
//        }
//    }
//}