package com.lvboaa.gulimall.ware.service.impl;

import com.lvboaa.common.exception.NoStockException;
import com.lvboaa.common.to.SkuHasStockVo;
import com.lvboaa.gulimall.ware.vo.LockStockResult;
import com.lvboaa.gulimall.ware.vo.OrderItemVo;
import com.lvboaa.gulimall.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvboaa.common.utils.PageUtils;
import com.lvboaa.common.utils.Query;

import com.lvboaa.gulimall.ware.dao.WareSkuDao;
import com.lvboaa.gulimall.ware.entity.WareSkuEntity;
import com.lvboaa.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> skuHasStockVos = skuIds.stream().map(item -> {
            Long count = this.baseMapper.getSkuStock(item);
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            skuHasStockVo.setSkuId(item);
            skuHasStockVo.setHasStock(count == null?false:count > 0);
            return skuHasStockVo;
        }).collect(Collectors.toList());
        return skuHasStockVos;
    }

    /**
     * 为某个订单锁定库存
     *
     * rollbackFor = NoStockException.class
     * 默认只要是运行时异常都会回滚
     * @param lockVo
     * @return
     */
    @Transactional(rollbackFor = NoStockException.class)
    @Override
    public void orderLockStock(WareSkuLockVo lockVo) {
        // 按照下单的收货地址，找到一个就近仓库，锁定库存
        // 1.找到每个商品在哪个仓库有库存
        List<OrderItemVo> locks = lockVo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            // 查询这个商品在哪有库存
            List<Long> wareIds = this.baseMapper.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());

        for (SkuWareHasStock stock:collect){
            Boolean skuStocked = false;
            Long skuId = stock.getSkuId();
            List<Long> wareIds = stock.getWareId();
            if (wareIds==null || wareIds.size() == 0){
                // 没有任何仓库有库存
               throw new NoStockException(skuId);
            }
            for (Long wareId:wareIds){
                // 成功返回1，失败返回0
                Long count = this.baseMapper.lockSkuStock(skuId, wareId, stock.getNum());
                if (count == 1){
                    skuStocked = true;
                    break;
                }
            }

            if (!skuStocked){
                // 当前商品没有锁住
                throw new NoStockException(skuId);
            }
        }
        // 只要没抛异常，肯定是全部锁成功的
    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }

}