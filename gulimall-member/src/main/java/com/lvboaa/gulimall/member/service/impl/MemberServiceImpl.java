package com.lvboaa.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lvboaa.common.exception.RRException;
import com.lvboaa.common.utils.HttpUtils;
import com.lvboaa.gulimall.member.dao.MemberLevelDao;
import com.lvboaa.gulimall.member.entity.MemberLevelEntity;
import com.lvboaa.gulimall.member.entity.MemberReceiveAddressEntity;
import com.lvboaa.gulimall.member.vo.MemberLoginVo;
import com.lvboaa.gulimall.member.vo.MemberRegisterVo;
import com.lvboaa.gulimall.member.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvboaa.common.utils.PageUtils;
import com.lvboaa.common.utils.Query;

import com.lvboaa.gulimall.member.dao.MemberDao;
import com.lvboaa.gulimall.member.entity.MemberEntity;
import com.lvboaa.gulimall.member.service.MemberService;


@Service("memberService")
@Slf4j
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterVo vo) {
        // 检查用户名和手机号是否唯一
        checkUserName(vo.getUserName());
        checkPhone(vo.getPhone());

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setUsername(vo.getUserName());
        memberEntity.setNickname(vo.getUserName());
        memberEntity.setMobile(vo.getPhone());

        // 密码不可逆加密
        // memberEntity.setPassword(MD5Utils.md5(vo.getPassword()));

        //spring的盐值加密 加随机盐加密：会根据加盐的密码自己解析出盐值 再进行比较
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        //passwordEncoder.matches("原密码","加密的密码");
        memberEntity.setPassword(encode);

        // 设置会员等价
        MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(levelEntity.getId());

        this.baseMapper.insert(memberEntity);
    }

    @Override
    public void checkUserName(String username) {
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count >0){
            throw new RRException("用户名已经存在");
        }
    }

    @Override
    public void checkPhone(String phone) {
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count >0){
            throw new RRException("手机号已被使用");
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("username", vo.getLoginacct())
                .or()
                .eq("mobile", vo.getLoginacct())
        );

        if (entity != null){
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(vo.getPassword(), entity.getPassword());
            if (matches){
                return entity;
            }
        }
        throw new RRException("用户名或密码错误");
    }

    @Override
    public MemberEntity login(SocialUser socialUser){
        // 登录注册合并逻辑
        String uid = socialUser.getUid();
        MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if (entity != null){
            // 这个用户已经注册
            entity.setAccessToken(socialUser.getAccess_token());
            entity.setExpiresIn(socialUser.getExpires_in());
            this.baseMapper.updateById(entity);

            return entity;
        }
        // 没有注册
        // 查询当前社交用户的社交账号信息（昵称、性别等）
        MemberEntity memberEntity = new MemberEntity();
        HashMap<String , String > map = new HashMap<>();
        map.put("access_token",socialUser.getAccess_token());
        map.put("uid",socialUser.getUid());
        try{
            HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), map);
            if (response.getStatusLine().getStatusCode() == 200){
                // 查询成功
                JSONObject jsonObject = JSON.parseObject(EntityUtils.toString(response.getEntity()));
                memberEntity.setUsername(jsonObject.getString("name"));
                memberEntity.setNickname(jsonObject.getString("name"));
                memberEntity.setGender("m".equals(jsonObject.getString("gender")) ? 1:0);
            }
        }catch (Exception e){
            // 即使网络出错也可以登录成功
            log.error("网络出错："+e.getMessage()+":"+map.toString());
            memberEntity.setNickname("微博用户_"+socialUser.getAccess_token().substring(0,5));
            memberEntity.setUsername(memberEntity.getNickname());
        }
        memberEntity.setSocialUid(socialUser.getUid());
        memberEntity.setAccessToken(socialUser.getAccess_token());
        memberEntity.setExpiresIn(socialUser.getExpires_in());
        this.baseMapper.insert(memberEntity);
        return memberEntity;
    }
}