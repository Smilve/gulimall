package com.lvboaa.gulimall.order.vo;

import com.lvboaa.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/10/11 11:13
 */
@Data
public class SubmitResponseVo {
    private OrderEntity order;

    /** 错误状态码 **/
    private Integer code; // 0就代表订单创建成功
}
