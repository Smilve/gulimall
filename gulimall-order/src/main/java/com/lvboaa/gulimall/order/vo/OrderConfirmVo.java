package com.lvboaa.gulimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Description: 订单确认页需要的数据
 *
 * @author lv.bo
 * @date 2021/10/8 18:04
 */
public class OrderConfirmVo {

    // 收货地址列表
    @Getter @Setter
    List<MemberAddressVo> address;

    // 所有选中的购物项
    @Getter @Setter
    List<OrderItemVo> items;

    // 优惠券信息
    @Getter @Setter
    Integer integration;

    /** 防止重复提交的令牌 **/
    @Getter @Setter
    private String orderToken;

    //BigDecimal total; // 订单总额

    //BigDecimal payPrice; // 应付价格

    @Getter @Setter
    Map<Long,Boolean> stocks; // 商品有货无货状态

    public Integer getCount() {
        Integer count = 0;
        if (items != null && items.size() > 0) {
            for (OrderItemVo item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    /** 订单总额 **/
    //计算订单总额
    public BigDecimal getTotal() {
        BigDecimal totalNum = BigDecimal.ZERO;
        if (items != null && items.size() > 0) {
            for (OrderItemVo item : items) {
                //计算当前商品的总价格
                BigDecimal itemPrice = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                //再计算全部商品的总价格
                totalNum = totalNum.add(itemPrice);
            }
        }
        return totalNum;
    }


    /** 应付价格 **/
    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
