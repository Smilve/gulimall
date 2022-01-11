package com.lvboaa.gulimall.order.to;

import com.lvboaa.gulimall.order.entity.OrderEntity;
import com.lvboaa.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/10/11 11:30
 */
@Data
public class OrderCreateTo {

    private OrderEntity order;

    private List<OrderItemEntity> orderItems;

    /** 订单计算的应付价格 **/
    private BigDecimal payPrice;

    /** 运费 **/
    private BigDecimal fare;
}
