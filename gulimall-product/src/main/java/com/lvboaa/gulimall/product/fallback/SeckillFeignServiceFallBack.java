package com.lvboaa.gulimall.product.fallback;

import com.lvboaa.common.exception.BizCodeEnum;
import com.lvboaa.common.utils.R;
import com.lvboaa.gulimall.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// 熔断保护
@Component
@Slf4j
public class SeckillFeignServiceFallBack implements SeckillFeignService {
    @Override
    public R getSkuSeckilInfo(Long skuId) {
        log.info("熔断调用");
        return R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(),BizCodeEnum.TO_MANY_REQUEST.getMessage());
    }
}
