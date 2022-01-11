package com.lvboaa.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Description: 订单提交的数据
 *
 * @author lv.bo
 * @date 2021/10/11 10:57
 */
@Data
public class OrderSubmitVo {
    /** 收获地址的id **/
    private Long addrId;

    /** 支付方式 **/
    private Integer payType;
    //无需提交要购买的商品，去购物车再获取一遍，因为可能在订单确认页的时候，用户又去购物车勾选了商品
    //优惠、发票

    /** 防重令牌 **/
    private String orderToken;

    /** 应付价格 **/
    // 虽然可以去购物车中获取，但可以把这个价格和购物车的价格对比，就是进行验价，如果价格不同，还可以提醒用户
    private BigDecimal payPrice;

    /** 订单备注 **/
    private String remarks;

    //用户相关的信息，直接去session中取出即可
}
