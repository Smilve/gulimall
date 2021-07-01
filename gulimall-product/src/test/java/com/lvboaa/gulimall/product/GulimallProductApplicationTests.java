package com.lvboaa.gulimall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lvboaa.gulimall.product.entity.BrandEntity;
import com.lvboaa.gulimall.product.service.BrandService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;
    @Test
    void contextLoads() {
//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setName("华为");
//        brandService.save(brandEntity);
//        System.out.println("保存成功");
        brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id",1L)).forEach(System.out::println);
    }

}
