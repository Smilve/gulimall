package com.lvboaa.gulimall.order.web;

import com.lvboaa.gulimall.order.service.OrderService;
import com.lvboaa.gulimall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.concurrent.ExecutionException;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/10/8 17:46
 */
@Controller
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
}
