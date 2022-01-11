package com.lvboaa.gulimall.order.web;

import com.lvboaa.common.exception.NoStockException;
import com.lvboaa.gulimall.order.service.OrderService;
import com.lvboaa.gulimall.order.vo.OrderConfirmVo;
import com.lvboaa.gulimall.order.vo.OrderSubmitVo;
import com.lvboaa.gulimall.order.vo.SubmitResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/10/8 17:46
 */
@Controller
@Slf4j
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        //订单确认页的数据
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("confirmOrderData",orderConfirmVo);
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo submitVo, Model model, RedirectAttributes redirectAttributes){
        SubmitResponseVo vo = null;
        try{
            vo = orderService.submitOrder(submitVo);
        }catch (NoStockException e){
            redirectAttributes.addFlashAttribute("msg","库存锁定失败，商品库存不足。");
            return "redirect:http://order.gulimall.com/toTrade";
        }catch (Exception e){
            log.error("",e);
            redirectAttributes.addFlashAttribute("msg","有错误。");
            return "redirect:http://order.gulimall.com/toTrade";
        }
        log.info("订单提交的数据："+vo);
        if (vo.getCode() == 0){
            // 下单成功去到支付页
            model.addAttribute("submitOrderResp",vo);
            return "pay";
        }
        // 下单失败回到订单确认页重新确认订单
        String msg = "下单失败；";
        switch (vo.getCode()){
            case 1: msg += "订单信息过期，请刷新再次提交。"; break;
            case 2: msg += "订单商品信息发送变化，请确认后再次提交。"; break;
            case 3: msg += "库存锁定失败，商品库存不足。";break;
        }
        redirectAttributes.addFlashAttribute("msg",msg);
        return "redirect:http://order.gulimall.com/toTrade";
    }
}
