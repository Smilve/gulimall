package com.lvboaa.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import com.lvboaa.gulimall.order.config.AlipayTemplate;
import com.lvboaa.gulimall.order.entity.OrderEntity;
import com.lvboaa.gulimall.order.service.OrderService;
import com.lvboaa.gulimall.order.vo.PayVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Slf4j
@Controller
public class PayWebController {

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private OrderService orderService;

//    @Autowired
//    private BestPayService bestPayService;

//    @Resource
//    private WxPayConfig wxPayConfig;

    /**
     * 用户下单:支付宝支付
     * 1、让支付页让浏览器展示
     * 2、支付成功以后，跳转到用户的订单列表页
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @ResponseBody
    @GetMapping(value = "/aliPayOrder",produces = "text/html")
    // produces 表示我会产生什么类型内容，这里直接html在页面显示成表单
    public String aliPayOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {

        PayVo payVo = orderService.getOrderPay(orderSn);
        if (payVo.getStatus() == 1){
            log.info("该订单已支付");
            return "该订单已支付";
        }else if (payVo.getStatus() == 4 || payVo.getStatus() == 5){
            log.info("该订单已失效");
            return "该订单已失效";
        }
        String pay = alipayTemplate.pay(payVo);
        log.info("支付："+pay);
        return pay;
    }


    /**
     * 微信支付
     * @param
     * @return
     */
//    @GetMapping(value = "/weixinPayOrder")
//    public String weixinPayOrder(@RequestParam("orderSn") String orderSn, Model model) {
//
//        OrderEntity orderInfo = orderService.getOrderByOrderSn(orderSn);
//
//        if (orderInfo == null) {
//            throw new RuntimeException("订单不存在");
//        }
//
//        PayRequest request = new PayRequest();
//        request.setOrderName("4559066-最好的支付sdk");
//        request.setOrderId(orderInfo.getOrderSn());
//        request.setOrderAmount(0.01);
//        request.setPayTypeEnum(WXPAY_NATIVE);
//
//        PayResponse payResponse = bestPayService.pay(request);
//        payResponse.setOrderId(orderInfo.getOrderSn());
//        log.info("发起支付 response={}", payResponse);
//
//        //传入前台的二维码路径生成支付二维码
//        model.addAttribute("codeUrl",payResponse.getCodeUrl());
//        model.addAttribute("orderId",payResponse.getOrderId());
//        model.addAttribute("returnUrl",wxPayConfig.getReturnUrl());
//
//        return "createForWxNative";
//    }


    //根据订单号查询订单状态的API
    @GetMapping(value = "/queryByOrderId")
    @ResponseBody
    public OrderEntity queryByOrderId(@RequestParam("orderId") String orderId) {
        log.info("查询支付记录...");
        return orderService.getOrderByOrderSn(orderId);
    }



}
