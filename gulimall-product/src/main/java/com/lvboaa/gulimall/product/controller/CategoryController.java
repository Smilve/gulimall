package com.lvboaa.gulimall.product.controller;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lvboaa.gulimall.product.entity.CategoryEntity;
import com.lvboaa.gulimall.product.service.CategoryService;
import com.lvboaa.common.utils.R;



/**
 * 商品三级分类
 *
 * @author lvbo
 * @email 2484420621@qq.com
 * @date 2021-06-30 23:33:00
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查出所有分类以及子分类，以树形结构组装起来
     */
    @RequestMapping("/list/tree")
    public R list(){
        List<CategoryEntity> entities = categoryService.listWithTree();

        return R.ok().put("data", entities);
    }



    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update/sort")
    //@RequiresPermissions("product:category:update")
    public R updateSort(@RequestBody CategoryEntity[] category){
        categoryService.updateBatchById(Arrays.asList(category));

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/testUpdate")
    public R testUpdate(){
        categoryService.testUpdate();

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] catIds){
        // 因为 Arrays.asList返回的 ArrayList是Arrays的内部类 而不是java.utils.ArrayList；没有add 和 remove这些方法，所以会抛出异常
        // 如果不转的话会报 java.lang.UnsupportedOperationException 的异常
		categoryService.removeMenuByIds(new CopyOnWriteArrayList<>(Arrays.asList(catIds)));
        return R.ok();
    }

}
