package com.lvboaa.authserver.controller;

import com.alibaba.fastjson.TypeReference;
import com.lvboaa.authserver.feign.MemberFeginService;
import com.lvboaa.authserver.service.SendSms;
import com.lvboaa.common.utils.JwtUtils;
import com.lvboaa.common.vo.MemberResponseVo;
import com.lvboaa.authserver.vo.UserLoginVo;
import com.lvboaa.authserver.vo.UserRegisterVo;
import com.lvboaa.common.constant.AuthServerConstant;
import com.lvboaa.common.exception.BizCodeEnum;
import com.lvboaa.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/9/23 14:47
 */

@Controller
@RequestMapping("/sms")
@Slf4j
public class IndexController {

    @Autowired
    private SendSms sendSms;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MemberFeginService memberFeginService;

    @GetMapping("/sendCode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone){
        //接口防刷
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)){
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000){
                // 60秒内不能再发请求
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION);
            }
        }

        //验证码的再次校验
        String code = sendSms.send(phone);
        //redis缓存验证码，防止同一个手机在60秒内发送验证码
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,code+"_"+System.currentTimeMillis(),5, TimeUnit.MINUTES);

        return R.ok(code);
    }

    @PostMapping("/register")
    public String register(@Valid UserRegisterVo vo, BindingResult result,
                           RedirectAttributes redirectAttributes){ // RedirectAttributes:重定向携带数据  利用Session原理，将数据放在session中
        // todo 分布式session问题
        //校验出错，回到注册页
        if (result.hasErrors()){
            Map<String, String> collect = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors",collect);
            return "redirect:/reg.html";
        }
        // 重定向不能使用model共享数据

        // 真正注册，调用远程服务器注册
        // 1.校验验证码
        String code = vo.getCode();
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        HashMap<String , Object> map = new HashMap<>();
        if (!StringUtils.isEmpty(code) && !StringUtils.isEmpty(redisCode)){
            if (code.equals(redisCode.split("_")[0])){ // 验证码通过
                // 删除验证码
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                // 注册
                R r = memberFeginService.register(vo);
                if (Integer.parseInt(r.get("code").toString()) != 0){
                    log.error(r.get("msg").toString());
                    map.put("msg",r.get("msg").toString());
                    redirectAttributes.addFlashAttribute("errors",map);
                    return "redirect:/reg.html";
                }
                return "redirect:/login.html";
            }
        }
        log.error("验证码错误");
        map.put("code","验证码错误");
        redirectAttributes.addFlashAttribute("errors",map);
        return "redirect:/reg.html";
    }

    @PostMapping("login")
    public String login(UserLoginVo vo, Model model, HttpSession session){
        // 远程登录
        R r = memberFeginService.login(vo);
        if (Integer.parseInt(r.get("code").toString()) != 0){
            log.error("登录失败："+r.getMsg());
            HashMap<Object, Object> map = new HashMap<>();
            map.put("msg",r.get("msg"));
            model.addAttribute("errors",map);
            return "/login.html";
        }
        MemberResponseVo responseVo = r.getData(new TypeReference<MemberResponseVo>() {
        });
        session.setAttribute(AuthServerConstant.LOGIN_USER,responseVo);
        log.info("登录成功,用户信息:\n"+responseVo.toString());

        return "redirect:http://gulimall.com";
    }
}
