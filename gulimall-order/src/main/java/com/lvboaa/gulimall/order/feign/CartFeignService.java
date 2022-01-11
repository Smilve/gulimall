package com.lvboaa.gulimall.order.feign;

import com.lvboaa.gulimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/10/8 18:40
 */
@FeignClient("gulimall-cart")
public interface CartFeignService {

    @GetMapping("/currentUserCartItems")
    List<OrderItemVo> getCurrentCartItems();
}
