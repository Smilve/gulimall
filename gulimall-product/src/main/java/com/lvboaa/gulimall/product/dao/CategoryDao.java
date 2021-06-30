package com.lvboaa.gulimall.product.dao;

import com.lvboaa.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author lvbo
 * @email 2484420621@qq.com
 * @date 2021-06-30 22:58:10
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
