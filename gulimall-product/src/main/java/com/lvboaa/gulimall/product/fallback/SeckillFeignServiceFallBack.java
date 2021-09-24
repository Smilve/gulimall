package com.lvboaa.gulimall.product.fallback;

import com.lvboaa.common.exception.BizCodeEnum;
import com.lvboaa.common.utils.R;
import com.lvboaa.gulimall.product.feign.SeckillFeignService;
import org.springframework.stereotype.Component;

@Component
public class SeckillFeignServiceFallBack implements SeckillFeignService {
    @Override
    public R getSkuSeckilInfo(Long skuId) {
        return R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(),BizCodeEnum.TO_MANY_REQUEST.getMessage());
    }
}
