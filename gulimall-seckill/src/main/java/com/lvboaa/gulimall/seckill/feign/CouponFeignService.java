package com.lvboaa.gulimall.seckill.feign;

import com.lvboaa.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/11/5 14:12
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    @GetMapping("/coupon/seckillsession/getLatest3DaysSession")
    R getLatest3DaysSession();
}
