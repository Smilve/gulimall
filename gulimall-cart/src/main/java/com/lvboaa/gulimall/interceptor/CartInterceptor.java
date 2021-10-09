package com.lvboaa.gulimall.interceptor;

import com.lvboaa.common.constant.AuthServerConstant;
import com.lvboaa.common.constant.CartConstant;
import com.lvboaa.common.vo.MemberResponseVo;
import com.lvboaa.gulimall.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * Description:在执行目标方法之前，判断用户的登录状态，并封装传递给目标请求
 *
 * @author lv.bo
 * @date 2021/9/28 10:13
 */

@Component
@Slf4j
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    // 目标方法执行之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info(request.getRequestURL().toString());
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberResponseVo responseVo = (MemberResponseVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (responseVo != null){
            // 用户登录
            userInfoTo.setUserId(responseVo.getId());
        }
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie:cookies){
            if (CartConstant.TEMP_USER_COOKIE_NAME.equals(cookie.getName())){
                userInfoTo.setUserKey(cookie.getValue());
                userInfoTo.setIsLogin(true);
                break;
            }
        }

        // 如果没有临时用户一定分配一个临时用户
        if (StringUtils.isEmpty(userInfoTo.getUserKey())){
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }

        // 目标方法执行之前
        threadLocal.set(userInfoTo);
        return true;
    }

    // 目标方法执行之后，分配临时用户让浏览器保存
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 让浏览器保存一个cookie 一个月后过期，京东没有延长cookie过期时间
        UserInfoTo userInfoTo = threadLocal.get();
        // 没有创建user-key的用户才设置cookie
        if (userInfoTo.getUserId() == null){
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}
