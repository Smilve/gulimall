package com.lvboaa.gulimall.member.service.impl;

import com.lvboaa.common.exception.RRException;
import com.lvboaa.gulimall.member.dao.MemberLevelDao;
import com.lvboaa.gulimall.member.entity.MemberLevelEntity;
import com.lvboaa.gulimall.member.vo.MemberLoginVo;
import com.lvboaa.gulimall.member.vo.MemberRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
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

}