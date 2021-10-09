package com.lvboaa.gulimall.order.interceptor;

import com.lvboaa.common.constant.AuthServerConstant;
import com.lvboaa.common.vo.MemberResponseVo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/10/8 17:47
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberResponseVo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MemberResponseVo attribute = (MemberResponseVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute != null){
            threadLocal.set(attribute);
            return true;
        }
        // 没登录 就去登录页面
        request.getSession().setAttribute("msg","请先登录");
        response.sendRedirect("http://auth.gulimall.com/login.html");
        return false;
    }
}
