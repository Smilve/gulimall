package com.lvboaa.search;

import com.alibaba.fastjson.JSON;
import com.lvboaa.search.config.ElasticSearchConfig;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GulimallSearchApplicationTests {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Test
    public void indexTest() throws IOException {
        //1.创建索引的请求
        CreateIndexRequest request = new CreateIndexRequest("lisen_index");
        //2客户端执行请求，请求后获得响应
        CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response);

    }

    @Test
    public void testIndexData() throws IOException {
        IndexRequest request = new IndexRequest("lisen_index");
        request.id("1");
        request.source(JSON.toJSONString(new User("test","asd")), XContentType.JSON);
        request.timeout("1s");
        IndexResponse response = restHighLevelClient.index(request, ElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(response.toString());
    }

    class User{
        private String name;
        private String age;

        public User(String name, String age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }
    }
}
