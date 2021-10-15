package com.lvboaa.gulimall.ware.vo;

import lombok.Data;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/10/11 14:46
 */
@Data
public class LockStockResult {
    private Long skuId;

    private Integer num;

    /** 是否锁定成功 **/
    private Boolean locked;
}
