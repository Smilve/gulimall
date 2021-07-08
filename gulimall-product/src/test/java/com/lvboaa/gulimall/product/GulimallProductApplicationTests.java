package com.lvboaa.gulimall.product;

import com.aliyun.oss.OSS;
import com.lvboaa.gulimall.product.service.BrandService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
class GulimallProductApplicationTests {


    @Autowired
    private OSS ossClient;

    @Test
    public String home() throws FileNotFoundException {
        ossClient.putObject("smilve", "1.jpg", new FileInputStream("D:\\1.jpg"));
        return "upload success";
    }
    @Autowired
    BrandService brandService;
    @Test
    void contextLoads() throws FileNotFoundException {


    }

}
