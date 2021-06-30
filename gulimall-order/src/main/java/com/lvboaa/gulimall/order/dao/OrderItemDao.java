package com.lvboaa.gulimall.order.dao;

import com.lvboaa.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author lvbo
 * @email 2484420621@qq.com
 * @date 2021-07-01 00:58:07
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
