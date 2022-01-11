package com.lvboaa.gulimall.seckill.controller;

import com.lvboaa.common.utils.R;
import com.lvboaa.gulimall.seckill.service.SecKillService;
import com.lvboaa.gulimall.seckill.to.SeckillSkuRedisTo;
import com.lvboaa.gulimall.seckill.vo.SeckillSkuVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/11/10 15:53
 */
@Controller
@Slf4j
public class SecKillController {

    @Autowired
    SecKillService secKillService;

    @GetMapping("/getCurrentSeckillSkus")
    @ResponseBody
    public R getCurrentSecKillSkus(){
        log.info("测试sentinel");
        List<SeckillSkuRedisTo> secList=secKillService.getCurrentSecKillSkus();
        return R.ok().setData(secList);
    }

    @GetMapping("/sku/seckill/{skuId}")
    @ResponseBody
    public R getSkuSeckilInfo(@PathVariable("skuId") Long skuId){
        SeckillSkuRedisTo seckillSkuRedisTo = secKillService.getSkuSeckilInfo(skuId);
        System.out.println("red:"+seckillSkuRedisTo.toString());
        return R.ok().setData(seckillSkuRedisTo);
    }

    //killId=2-2&key=878065d101dd4bfaa62faf3504bb6caa&num=1
    @GetMapping("kill")
    public String secKill(@RequestParam("killId")String killId,
                          @RequestParam("key")String key,
                          @RequestParam("num")Integer num,
                          Model model) {
        // 登录判断，拦截器帮我们做了
        String orderSn = secKillService.secKill(killId,key,num);
        System.out.println(orderSn);
        model.addAttribute("orderSn",orderSn);
        return "success";
    }

}
