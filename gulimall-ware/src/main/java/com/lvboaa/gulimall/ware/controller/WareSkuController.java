package com.lvboaa.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.lvboaa.common.exception.BizCodeEnum;
import com.lvboaa.common.exception.NoStockException;
import com.lvboaa.common.to.SkuHasStockVo;
import com.lvboaa.gulimall.ware.vo.LockStockResult;
import com.lvboaa.gulimall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lvboaa.gulimall.ware.entity.WareSkuEntity;
import com.lvboaa.gulimall.ware.service.WareSkuService;
import com.lvboaa.common.utils.PageUtils;
import com.lvboaa.common.utils.R;



/**
 * 商品库存
 *
 * @author lvbo
 * @email 2484420621@qq.com
 * @date 2021-07-01 01:05:08
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 查询sku是否有库存
     * @return
     */
    @PostMapping("/hasStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds) {

        //skuId stock
        List<SkuHasStockVo> vos = wareSkuService.getSkuHasStock(skuIds);

        return R.ok().setData(vos);

    }

    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo lockVo){
        try{
            wareSkuService.orderLockStock(lockVo);
            return R.ok();
        }catch (NoStockException e){
            return R.error(BizCodeEnum.NO_STOCK_EXCEPTION);
        }
    }
}
