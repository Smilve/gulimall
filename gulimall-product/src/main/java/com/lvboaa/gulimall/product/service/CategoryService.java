package com.lvboaa.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvboaa.common.utils.PageUtils;
import com.lvboaa.gulimall.product.entity.CategoryEntity;
import com.lvboaa.gulimall.product.vo.Catelog2Vo;

import javax.jws.Oneway;
import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author lvbo
 * @email 2484420621@qq.com
 * @date 2021-06-30 22:58:10
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    void testUpdate();

    List<CategoryEntity> getLevel1Category();

    Map<String, List<Catelog2Vo>> getCatalogJson();
}

