package com.lvboaa.gulimall.product.dao;

import com.lvboaa.gulimall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author lvbo
 * @email 2484420621@qq.com
 * @date 2021-06-30 22:58:09
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
