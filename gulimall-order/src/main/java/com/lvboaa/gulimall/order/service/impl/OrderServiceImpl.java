package com.lvboaa.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.lvboaa.common.utils.R;
import com.lvboaa.common.vo.MemberResponseVo;
import com.lvboaa.gulimall.order.feign.CartFeignService;
import com.lvboaa.gulimall.order.feign.MemberFeignService;
import com.lvboaa.gulimall.order.feign.WareFeignService;
import com.lvboaa.gulimall.order.interceptor.LoginUserInterceptor;
import com.lvboaa.gulimall.order.vo.MemberAddressVo;
import com.lvboaa.gulimall.order.vo.OrderConfirmVo;
import com.lvboaa.gulimall.order.vo.OrderItemVo;
import com.lvboaa.gulimall.order.vo.SkuStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvboaa.common.utils.PageUtils;
import com.lvboaa.common.utils.Query;

import com.lvboaa.gulimall.order.dao.OrderDao;
import com.lvboaa.gulimall.order.entity.OrderEntity;
import com.lvboaa.gulimall.order.service.OrderService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import sun.nio.ch.ThreadPool;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberResponseVo responseVo = LoginUserInterceptor.threadLocal.get();

        //获取当前线程请求头信息(解决Feign异步调用丢失请求头问题)
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        // 查询所有收货地址
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            //每一个线程都来共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> allAddress = memberFeignService.getAllAddress(responseVo.getId());
            confirmVo.setAddress(allAddress);
        }, executor);

        // 查询购物车中选中的购物项
        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> currentCartItems = cartFeignService.getCurrentCartItems();
            confirmVo.setItems(currentCartItems);
        }, executor).thenRunAsync(()->{
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> skuIds = items.stream().map(e -> e.getSkuId()).collect(Collectors.toList());

            //远程查询商品库存信息
            R skuHasStock = wareFeignService.getSkuHasStock(skuIds);
            List<SkuStockVo> skuStockVos = skuHasStock.getData("data", new TypeReference<List<SkuStockVo>>() {});

            if (skuStockVos != null && skuStockVos.size() > 0) {
                //将skuStockVos集合转换为map
                Map<Long, Boolean> skuHasStockMap = skuStockVos.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(skuHasStockMap);
            }
        });


        // 查询用户积分
        confirmVo.setIntegration(responseVo.getIntegration());

        // 其他查找

        // todo: 防重令牌
        CompletableFuture.allOf(addressFuture,cartFuture).get();

        return confirmVo;
    }

}