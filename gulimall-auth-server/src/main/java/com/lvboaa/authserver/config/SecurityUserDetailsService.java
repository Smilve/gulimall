package com.lvboaa.authserver.config;

import com.alibaba.fastjson.TypeReference;
import com.lvboaa.authserver.dto.UserDto;
import com.lvboaa.authserver.feign.MemberFeginService;
import com.lvboaa.authserver.mapper.UserMapper;
import com.lvboaa.common.vo.MemberResponseVo;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SecurityUserDetailsService implements UserDetailsService {

    @Autowired
    MemberFeginService memberFeginService;

    @Autowired
    UserMapper userMapper;

    //根据 账号查询用户信息
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //将来连接数据库根据账号查询用户信息
        MemberResponseVo member = memberFeginService.getUserByUserName(username).getData("member",new TypeReference<MemberResponseVo>(){});
        log.info("登录用户："+member.getId().toString());
        if(member == null){
            //如果用户查不到，返回null，由provider来抛出异常
            return null;
        }
        //根据用户的id查询用户的权限
        List<String> permissions = userMapper.findPermissionsByUserId(member.getId().toString());
        //将permissions转成数组
        String[] permissionArray = new String[permissions.size()];
        permissions.toArray(permissionArray);
        UserDetails userDetails = User.withUsername(member.getUsername()).password(member.getPassword()).authorities(permissionArray).build();
        return userDetails;
    }

//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//
//        //将来连接数据库根据账号查询用户信息
//        UserDto userDto = userMapper.getUserByUsername(username);
//        if(userDto == null){
//            //如果用户查不到，返回null，由provider来抛出异常
//            return null;
//        }
//        //根据用户的id查询用户的权限
//        List<String> permissions = userMapper.findPermissionsByUserId(userDto.getId());
//        //将permissions转成数组
//        String[] permissionArray = new String[permissions.size()];
//        permissions.toArray(permissionArray);
//        UserDetails userDetails = User.withUsername(userDto.getUsername()).password(userDto.getPassword()).authorities(permissionArray).build();
//        return userDetails;
//    }
}