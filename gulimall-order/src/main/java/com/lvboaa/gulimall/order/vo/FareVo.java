package com.lvboaa.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/10/11 11:44
 */
@Data
public class FareVo {
    private MemberAddressVo address;

    private BigDecimal fare;
}
