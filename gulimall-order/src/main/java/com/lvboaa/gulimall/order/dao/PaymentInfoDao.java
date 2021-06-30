package com.lvboaa.gulimall.order.dao;

import com.lvboaa.gulimall.order.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author lvbo
 * @email 2484420621@qq.com
 * @date 2021-07-01 00:58:06
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
