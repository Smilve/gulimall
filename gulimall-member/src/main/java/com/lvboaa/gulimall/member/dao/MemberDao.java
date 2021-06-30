package com.lvboaa.gulimall.member.dao;

import com.lvboaa.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author lvbo
 * @email 2484420621@qq.com
 * @date 2021-07-01 00:48:54
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
