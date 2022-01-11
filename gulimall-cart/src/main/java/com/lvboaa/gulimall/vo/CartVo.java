package com.lvboaa.gulimall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Description: 整个购物车需要计算的属性，必须重写get方法，保证每次获得属性都会进行计算
 *    gulimall:cart:userid,{
 *        skuInfoId1, cartItem;
 *        skuInfoId2, cartItem;
 *    }
 *    Map<String k1,Map<String k2,CartItemInfo>>
 * @author lv.bo
 * @date 2021/9/28 9:41
 */

public class CartVo {

    private List<CartItemVo> items;

    private Integer countNum; //商品数量

    private Integer countType; //商品类型数量

    private BigDecimal totalAmount; // 商品总价

    private BigDecimal reduce = new BigDecimal(0); // 减免价格

    public List<CartItemVo> getItems() {
        return items;
    }

    public void setItems(List<CartItemVo> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        int count = 0;
        if (items != null && items.size() > 0){
            for (CartItemVo item:items){
                count += item.getCount();
            }

        }
        return count;
    }

    public Integer getCountType() {
        int count = 0;
        if (items != null && items.size() > 0){
            count = items.size();
        }
        return count;
    }


    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal(0);
        if (items != null && items.size() > 0){
            for (CartItemVo item:items){
                // 总价就等于每个item的总价格相加
                if (item.getCheck()){
                    amount = amount.add(item.getTotalPrice());
                }
            }

        }

        // 还需要减去优惠的价格
        return amount.subtract(getReduce());
    }


    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
