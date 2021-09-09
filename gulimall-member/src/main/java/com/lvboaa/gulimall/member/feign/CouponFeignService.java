package com.lvboaa.gulimall.member.feign;

import com.lvboaa.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: lvbo
 * @date: 2021/7/1 21:56
 * @description:
 */
// 服务名
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    // 对应路径
    @RequestMapping("/coupon/coupon/member/list")
    R memberCoupons();

}
