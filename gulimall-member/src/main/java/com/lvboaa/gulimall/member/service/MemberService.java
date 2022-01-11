package com.lvboaa.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvboaa.common.utils.PageUtils;
import com.lvboaa.gulimall.member.entity.MemberEntity;
import com.lvboaa.gulimall.member.vo.MemberLoginVo;
import com.lvboaa.gulimall.member.vo.MemberRegisterVo;
import com.lvboaa.gulimall.member.vo.SocialUser;

import java.util.List;
import java.util.Map;

/**
 * 会员
 *
 * @author lvbo
 * @email 2484420621@qq.com
 * @date 2021-07-01 00:48:54
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVo vo);

    void checkUserName(String username);

    void checkPhone(String phone);

    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SocialUser socialUser);
}

