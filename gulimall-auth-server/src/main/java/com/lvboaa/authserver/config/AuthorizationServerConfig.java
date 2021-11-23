package com.lvboaa.authserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
//开启oauth2,auth server模式
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    //配置客户端详情服务，初始化客户端信息用以验证
//    @Override
//    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.inMemory()
//                //client的id和密码
//                .withClient("client1")
//                .secret(passwordEncoder.encode("123123"))
//                //给client一个id,这个在client的配置里要用的
//                .resourceIds("resource1")
//
//                //允许的申请token的方式,测试用例在test项目里都有.
//                //authorization_code授权码模式,这个是标准模式
//                //implicit简单模式,这个主要是给无后台的纯前端项目用的
//                //password密码模式,直接拿用户的账号密码授权,不安全
//                //client_credentials客户端模式,用clientid和密码授权,和用户无关的授权方式
//                //refresh_token使用有效的refresh_token去重新生成一个token,之前的会失效
//                .authorizedGrantTypes("authorization_code", "password", "client_credentials", "implicit", "refresh_token")
//
//                //授权的范围,每个resource会设置自己的范围.
//                .scopes("scope1")
//
//                //这个是设置要不要弹出确认授权页面的.
//                .autoApprove(false)
//                //这个相当于是client的域名,重定向给code的时候会跳转这个域名
//                .redirectUris("http://www.baidu.com")
//                .and()
//                //在spring cloud的测试中,我们有两个资源服务,这里也给他们配置两个client,并分配不同的scope.
//                .withClient("client2")
//                .secret(passwordEncoder.encode("123123"))
//                .resourceIds("resource2")
//                .authorizedGrantTypes("authorization_code", "password", "client_credentials", "implicit", "refresh_token")
//                .scopes("scope2")
//                .autoApprove(false)
//                .redirectUris("http://www.sogou.com");
//    }

    // 从数据库中读取
    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
        clients.withClientDetails(detailsService);
    }



    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private ClientDetailsService detailsService;

    @Autowired
    private JwtAccessTokenConverter accessTokenConverter;

    //配置token管理服务
    @Bean
    public AuthorizationServerTokenServices tokenService() {
        DefaultTokenServices service=new DefaultTokenServices();
        service.setClientDetailsService(detailsService);
        service.setSupportRefreshToken(true);
        service.setTokenStore(tokenStore); //绑定tokenStore

        //配置token的存储方法
        service.setTokenStore(tokenStore);
        service.setAccessTokenValiditySeconds(7200); // 令牌默认有效期2小时
        service.setRefreshTokenValiditySeconds(259200); // 刷新令牌默认有效期3天

        // 配置token生成使用jwt
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(accessTokenConverter));
        service.setTokenEnhancer(tokenEnhancerChain);

        return service;
    }

    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;

    //密码模式才需要配置,认证管理器
    @Autowired
    private AuthenticationManager authenticationManager;

    //把上面的各个组件组合在一起
    /* 框架的默认url链接，可通过pathMapping()进行替换，第一个参数是默认的，第二个是自己要替换的
    /oauth/authorize：授权端点。
    /oauth/token：令牌端点。
    /oauth/confirm_access：用户确认授权提交端点。
    /oauth/error：授权服务错误信息端点。
    /oauth/check_token：用于资源服务访问的令牌解析端点。
    /oauth/token_key：提供公有密匙的端点，如果你使用JWT令牌的话。
     */
    // 配置访问端点和令牌服务(令牌的生成及保存策略)
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .authenticationManager(authenticationManager)
                .authorizationCodeServices(authorizationCodeServices)
                .tokenServices(tokenService())
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }

//    @Bean
//    public AuthorizationCodeServices authorizationCodeServices() { //设置授权码模式的授权码如何存取，暂时采用内存方式
//        return new InMemoryAuthorizationCodeServices();
//    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) {
        return new JdbcAuthorizationCodeServices(dataSource);//设置授权码模式的授权码存到数据库
    }


    //配置哪些接口可以被访问 访问端点的安全约束
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security){
        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients()
        ;
    }



}