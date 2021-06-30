package com.lvboaa.gulimall.order.dao;

import com.lvboaa.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author lvbo
 * @email 2484420621@qq.com
 * @date 2021-07-01 00:58:07
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
