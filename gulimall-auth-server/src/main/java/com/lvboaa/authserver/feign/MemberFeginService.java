package com.lvboaa.authserver.feign;

import com.lvboaa.authserver.vo.SocialUser;
import com.lvboaa.authserver.vo.UserLoginVo;
import com.lvboaa.authserver.vo.UserRegisterVo;
import com.lvboaa.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("gulimall-member")
public interface MemberFeginService {

    @PostMapping("/member/member/register")
    R register(UserRegisterVo vo);

    @PostMapping("/member/member/login")
    R login(UserLoginVo vo);

    @PostMapping("/member/member/oauth2/login")
    R oauthLogin(SocialUser socialUser);
}
