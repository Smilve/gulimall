package com.lvboaa.gulimall.order.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * Description: 使用seata的DataSourceProxy 代理自己的数据源
 *
 * @author lv.bo
 * @date 2021/10/15 14:23
 */
//@Configuration
//public class SeataConfig {
//
//    @Autowired
//    DataSourceProperties dataSourceProperties;
//
//
//    @Bean
//    public DataSource dataSource(){
//        HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//        if (StringUtils.hasText(dataSourceProperties.getName())) {
//            dataSource.setPoolName(dataSourceProperties.getName());
//        }
//
//        return new DataSourceProxy(dataSource);
//    }
//
//}
