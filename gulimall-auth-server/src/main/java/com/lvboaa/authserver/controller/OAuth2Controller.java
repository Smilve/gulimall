package com.lvboaa.authserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lvboaa.authserver.feign.MemberFeginService;
import com.lvboaa.common.constant.AuthServerConstant;
import com.lvboaa.common.utils.JwtUtils;
import com.lvboaa.common.vo.MemberResponseVo;
import com.lvboaa.authserver.vo.SocialUser;
import com.lvboaa.common.utils.HttpUtils;
import com.lvboaa.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * Description: 处理社交登录
 *
 * @author lv.bo
 * @date 2021/9/24 11:38
 */
@RequestMapping("/oauth2.0")
@Slf4j
@Controller
public class OAuth2Controller {

    @Autowired
    MemberFeginService memberFeginService;

    @GetMapping("/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
        HashMap<String, String > map = new HashMap<>();
        map.put("client_id","432122344");
        map.put("client_secret","fd8f5ccaaed65147916c7eee2e0f03ee");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("code",code);
        log.info("获取的code:"+code);
        //根据code换取accessToken
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", new HashMap<>(), map, new HashMap<>());
        if (response.getStatusLine().getStatusCode() == 200){
            //获取到accessToken
            SocialUser socialUser = JSON.parseObject(EntityUtils.toString(response.getEntity()), SocialUser.class);

            // 知道是哪个社交用户
            // 如果用户是第一次进入网站，自动注册用户，这个社交账号需要对应一个用户，下次进入网站就不用注册了
            // 登录或注册这个用户
            R r = memberFeginService.oauthLogin(socialUser);
            if (r.getCode() == 0){
                // 登录成功跳回首页
                MemberResponseVo responseVo = r.getData(new TypeReference<MemberResponseVo>() {});
                log.info("登录成功，用户信息：\n"+responseVo.toString());

                session.setAttribute(AuthServerConstant.LOGIN_USER,responseVo);
                return "redirect:http://gulimall.com";
            }
        }
        //登录失败
        return "redirect:http://auth.gulimall.com/login.html";
    }
}
