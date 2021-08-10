package com.lvboaa.gulimall.product.service.impl;

import com.mysql.cj.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvboaa.common.utils.PageUtils;
import com.lvboaa.common.utils.Query;

import com.lvboaa.gulimall.product.dao.AttrDao;
import com.lvboaa.gulimall.product.entity.AttrEntity;
import com.lvboaa.gulimall.product.service.AttrService;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catalogId) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        if (catalogId != 0){
            queryWrapper.eq("catalog_id",catalogId);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isNullOrEmpty(key)){
            queryWrapper.and((wrapper)->{
                wrapper.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

}