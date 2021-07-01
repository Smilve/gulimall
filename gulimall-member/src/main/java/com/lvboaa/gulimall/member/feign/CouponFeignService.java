package com.lvboaa.gulimall.member.feign;

import com.lvboaa.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: lvbo
 * @date: 2021/7/1 21:56
 * @description:
 */

@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    @RequestMapping("/coupon/coupon/member/list")
    public R memberCoupons();

}
