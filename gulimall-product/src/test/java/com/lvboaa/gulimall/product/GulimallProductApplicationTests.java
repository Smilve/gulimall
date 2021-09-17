package com.lvboaa.gulimall.product;

//import com.aliyun.oss.OSS;
import com.lvboaa.gulimall.product.service.BrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RunWith(value= SpringJUnit4ClassRunner.class)
@SpringBootTest(classes={GulimallProductApplication.class})
public class GulimallProductApplicationTests {


//    @Autowired
//    private OSS ossClient;
//
//    @Test
//    public String home() throws FileNotFoundException {
//        ossClient.putObject("smilve", "1.jpg", new FileInputStream("D:\\1.jpg"));
//        return "upload success";
//    }
//    @Autowired
//    BrandService brandService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Test
    public void contextLoads() throws FileNotFoundException {


    }

    @Test
    public void testRedis(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello","world"+ UUID.randomUUID().toString());
        System.out.println(ops.get("hello"));
    }

    @Test
    public void testRedisson(){
        System.out.println(redissonClient);

    }
}
