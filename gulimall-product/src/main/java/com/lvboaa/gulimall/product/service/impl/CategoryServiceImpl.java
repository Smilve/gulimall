package com.lvboaa.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvboaa.common.utils.PageUtils;
import com.lvboaa.common.utils.Query;

import com.lvboaa.gulimall.product.dao.CategoryDao;
import com.lvboaa.gulimall.product.entity.CategoryEntity;
import com.lvboaa.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    List<Long> catIdList = null;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //组装成树形结构
        //  找到一级分类
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map(menu -> {
            //找到子菜单
            menu.setChildren(getChildrens(menu,entities));
            return menu;
        }).sorted((m1,m2)->
                (m1.getSort() == null?0:m1.getSort()) - (m2.getSort() == null?0:m2.getSort())
        ).collect(Collectors.toList());

        return entities;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //检查当前删除的菜单，是否被别的地方引用（是否是别的父类别，是的话需要都查出来删除）
        List<CategoryEntity> entities = baseMapper.selectList(null);

        catIdList = new CopyOnWriteArrayList<>();
        asList.stream().forEach(id->
                getDelIds(id,entities));
        catIdList.addAll(asList);
        //list去重 但是mybatis-plus会自动去重
        //catIdList = new CopyOnWriteArrayList<>(new HashSet<>(catIdList));

        // 逻辑删除
        baseMapper.deleteBatchIds(catIdList);
    }

    public void getDelIds(Long id,List<CategoryEntity> all){
        List<Long> collect = all.stream().filter(ce ->
                ce.getParentCid().equals(id)
        ).map(ce->{
           getDelIds(ce.getCatId(),all);
           return ce.getCatId();
        }).collect(Collectors.toList());
        catIdList.addAll(collect);
    }

    public List<CategoryEntity> getChildrens(CategoryEntity root,List<CategoryEntity> all){
        List<CategoryEntity> children = all.stream().filter(categoryEntity ->
            categoryEntity.getParentCid().equals(root.getCatId())
        ).map(categoryEntity -> {
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((m1, m2) ->
                (m1.getSort() == null?0:m1.getSort()) - (m2.getSort() == null?0:m2.getSort())
        ).collect(Collectors.toList());

        return children;
    }


}