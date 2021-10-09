package com.lvboaa.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvboaa.common.utils.PageUtils;
import com.lvboaa.gulimall.ware.entity.WareInfoEntity;
import com.lvboaa.gulimall.ware.vo.FareVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author lvbo
 * @email 2484420621@qq.com
 * @date 2021-07-01 01:05:08
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     *  根据收货地址得到运费
     * @param addrId	
     * @return
     * @author lv.bo
     * @date 2021/10/9 17:24
     */
    FareVo getFare(Long addrId);
}

