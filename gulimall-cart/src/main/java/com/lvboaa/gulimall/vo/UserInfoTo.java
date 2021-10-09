package com.lvboaa.gulimall.vo;

import lombok.Data;
import lombok.ToString;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/9/28 10:18
 */

@Data
@ToString
public class UserInfoTo {
    private Long userId;

    private String userKey;

    private Boolean isLogin = false;
}
