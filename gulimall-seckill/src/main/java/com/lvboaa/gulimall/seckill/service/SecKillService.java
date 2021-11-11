package com.lvboaa.gulimall.seckill.service;

import com.lvboaa.gulimall.seckill.to.SeckillSkuRedisTo;

import java.util.List;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/11/5 13:59
 */
public interface SecKillService {


    void uploadSeckillSkuLatest3Days();

    List<SeckillSkuRedisTo> getCurrentSecKillSkus();

    SeckillSkuRedisTo getSkuSeckilInfo(Long skuId);

    String secKill(String killId, String key, Integer num);
}
