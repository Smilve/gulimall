package com.lvboaa.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvboaa.common.utils.PageUtils;
import com.lvboaa.gulimall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author lvbo
 * @email 2484420621@qq.com
 * @date 2021-07-01 01:05:08
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);

    WareOrderTaskEntity getOrderTaskByOrderSn(String orderSn);
}

