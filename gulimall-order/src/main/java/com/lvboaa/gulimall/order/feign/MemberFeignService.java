package com.lvboaa.gulimall.order.feign;

import com.lvboaa.gulimall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/10/8 18:17
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {
    @GetMapping("/member/memberreceiveaddress/{memberId}/address")
    List<MemberAddressVo> getAllAddress(@PathVariable("memberId") Long memberId);

}
