package com.lvboaa.gulimall.member.interceptor;

import com.lvboaa.common.constant.AuthServerConstant;
import com.lvboaa.common.vo.MemberResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/10/8 17:47
 */
@Component
@Slf4j
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberResponseVo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println("进来了");
        String uri = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/member/**", uri);
        boolean match1 = new AntPathMatcher().match("/user/**", uri);
        if (match || match1) {
            return true;
        }
        log.info("拦截请求："+uri);

        MemberResponseVo attribute = (MemberResponseVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute != null){
            threadLocal.set(attribute);
            return true;
        }
        // 未登录，返回登录页面
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<script>alert('请先进行登录，再进行后续操作！');location.href='http://auth.gulimall.com/login.html'</script>");
        // session.setAttribute("msg", "请先进行登录");
        // response.sendRedirect("http://auth.gulimall.com/login.html");
        return false;
    }
}
