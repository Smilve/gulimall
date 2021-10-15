package com.lvboaa.gulimall.order.feign;

import com.lvboaa.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/10/11 13:50
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {

    @GetMapping("/product/spuinfo/skuId/{skuId}")
    R getSupInfoBySkuId(@PathVariable("skuId")Long skuId);
}
