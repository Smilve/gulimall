package com.lvboaa.gulimall.ware.dao;

import com.lvboaa.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author lvbo
 * @email 2484420621@qq.com
 * @date 2021-07-01 01:05:08
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    Long getSkuStock(Long item);

    List<Long> listWareIdHasSkuStock(@Param("skuId") Long skuId);

    /**
     * 锁定库存
     * @param skuId
     * @param wareId
     * @param num
     * @return
     */
    Long lockSkuStock(@Param("skuId")Long skuId, @Param("wareId")Long wareId, @Param("num")Integer num);

    /**
     * 解锁库存
     * @param skuId
     * @param wareId
     * @param num
     */
    void unLockStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);

}
