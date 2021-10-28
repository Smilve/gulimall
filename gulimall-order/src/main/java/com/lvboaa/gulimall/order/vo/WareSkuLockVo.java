package com.lvboaa.gulimall.order.vo;

import lombok.Data;

import java.util.List;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/10/11 14:35
 */
@Data
public class WareSkuLockVo {
    private String orderSn;

    /** 需要锁住的所有库存信息 **/
    private List<OrderItemVo> locks;
}