package com.lvboaa.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description: es配置
 *
 * @author lv.bo
 * @date 2021/8/13 13:53
 */
@Configuration
public class ElasticSearchConfig {

//    @Value("es.server.url")
//    private String url;

//    @Value("es.server.port")
//    private Integer port;

//    @Value("es.server.scheme")
//    private String scheme;


    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        // TODO：修改为线上的地址
        return new RestHighLevelClient(RestClient.builder(new HttpHost("127.0.0.1",9200,"http")));
    }
}
