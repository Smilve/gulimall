package com.lvboaa.authserver.controller;

import com.lvboaa.common.constant.AuthServerConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/9/24 18:49
 */
@Controller
public class LoginController {
    @GetMapping("/login.html")
    public String loginPage(HttpSession session){

        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute == null){
            return "login";
        }
        return "redirect:http://gulimall.com";
    }

    @GetMapping("/loguot.html")
    public String loguot(HttpSession session){
        session.removeAttribute(AuthServerConstant.LOGIN_USER);
        session.invalidate();
        return "redirect:http://gulimall.com";
    }

}
