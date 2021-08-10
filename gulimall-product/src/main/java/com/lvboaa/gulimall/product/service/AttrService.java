package com.lvboaa.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvboaa.common.utils.PageUtils;
import com.lvboaa.gulimall.product.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author lvbo
 * @email 2484420621@qq.com
 * @date 2021-06-30 22:58:10
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catalogId);
}

