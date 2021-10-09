package com.lvboaa.gulimall.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lvboaa.common.constant.CartConstant;
import com.lvboaa.common.exception.BizCodeEnum;
import com.lvboaa.common.exception.RRException;
import com.lvboaa.common.utils.R;
import com.lvboaa.gulimall.feign.ProductFeignService;
import com.lvboaa.gulimall.interceptor.CartInterceptor;
import com.lvboaa.gulimall.service.CartService;
import com.lvboaa.gulimall.vo.CartItemVo;
import com.lvboaa.gulimall.vo.CartVo;
import com.lvboaa.gulimall.vo.SkuInfoVo;
import com.lvboaa.gulimall.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/9/28 9:59
 */

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        //判断Redis是否有该商品的信息
        String productRedisValue = (String) cartOps.get(skuId.toString());
        //如果没有就添加数据
        if (StringUtils.isEmpty(productRedisValue)) {

            //2、添加新的商品到购物车(redis)
            CartItemVo cartItemVo = new CartItemVo();
            //开启第一个异步任务
            CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {
                //1、远程查询当前要添加商品的信息
                R productSkuInfo = productFeignService.getInfo(skuId);
                SkuInfoVo skuInfo = productSkuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {});
                //数据赋值操作
                cartItemVo.setSkuId(skuInfo.getSkuId());
                cartItemVo.setTitle(skuInfo.getSkuTitle());
                cartItemVo.setImage(skuInfo.getSkuDefaultImg());
                cartItemVo.setPrice(skuInfo.getPrice());
                cartItemVo.setCount(num);
            }, executor);

            //开启第二个异步任务
            CompletableFuture<Void> getSkuAttrValuesFuture = CompletableFuture.runAsync(() -> {
                //2、远程查询skuAttrValues组合信息
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItemVo.setSkuAttr(skuSaleAttrValues);
            }, executor);

            //等待所有的异步任务全部完成 阻塞等待
            CompletableFuture.allOf(getSkuInfoFuture,getSkuAttrValuesFuture).get();

            String cartItemJson = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(), cartItemJson);

            return cartItemVo;
        } else {
            //购物车有此商品，修改数量即可
            CartItemVo cartItemVo = JSON.parseObject(productRedisValue, CartItemVo.class);
            cartItemVo.setCount(cartItemVo.getCount() + num);
            //修改redis的数据
            String cartItemJson = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(),cartItemJson);

            return cartItemVo;
        }
    }

    @Override
    public CartItemVo getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String redisValue = (String) cartOps.get(skuId.toString());
        CartItemVo cartItemVo = JSONObject.parseObject(redisValue, CartItemVo.class);
        return cartItemVo;
    }

    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {

        CartVo cartVo = new CartVo();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null) {
            //1、登录
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
            //临时购物车的键
            String temptCartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();

            //2、如果临时购物车的数据还未进行合并
            List<CartItemVo> tempCartItems = getCartItems(temptCartKey);
            if (tempCartItems != null) {
                //临时购物车有数据需要进行合并操作 模拟页面传入参数 直接合并
                for (CartItemVo item : tempCartItems) {
                    addToCart(item.getSkuId(),item.getCount());
                }
                //清除临时购物车的数据
                clearCart(temptCartKey);
            }

            //3、获取登录后的购物车数据【包含合并过来的临时购物车的数据和登录后购物车的数据】
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);
        } else {
            //没登录
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
            //获取临时购物车里面的所有购物项
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);
        }

        return cartVo;
    }

    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCheck(check==1?true:false);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteIdCartInfo(Integer skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItemVo> getUserCartItems() {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() == null){
            // 未登录
            return null;
        }else {
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
        }

        List<CartItemVo> cartItems = getCartItems(cartKey);
        if (cartItems == null){
            throw new RRException(BizCodeEnum.CART_HAVENOT_ITEM);
        }

        // 筛选出选中的
        List<CartItemVo> cartItemList = cartItems.stream()
                .filter(item->item.getCheck())
                .map(item->{
                    // 更新为最新的价格(查询数据库)
                    BigDecimal price = productFeignService.getPrice(item.getSkuId());
                    item.setPrice(price);
                    cartOps.put(item.getSkuId().toString(),JSON.toJSONString(item)); // 更新价格信息
                    return item;
                })
                .collect(Collectors.toList());

        return cartItemList;
    }

    /**
     * 获取操作redis hash对象  类似于把这个 redisTemplate.opsForValue() 合并了；操作map结果的数据更方便
     * 类似于得到  Map<String k1,Map<String k2,CartItemInfo>> 的 Map<String k2,CartItemInfo> 直接通过k2就可以增删改查
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps(){
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null){
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
        }else {
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
        }

        return redisTemplate.boundHashOps(cartKey);
    }

    /**
     * 获取购物车里面的数据
     * @param cartKey
     * @return
     */
    private List<CartItemVo> getCartItems(String cartKey) {
        //获取购物车里面的所有商品
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if (values != null && values.size() > 0) {
            List<CartItemVo> cartItemVoStream = values.stream().map((obj) -> {
                String str = (String) obj;
                CartItemVo cartItem = JSON.parseObject(str, CartItemVo.class);
                return cartItem;
            }).collect(Collectors.toList());
            return cartItemVoStream;
        }
        return null;

    }
}
