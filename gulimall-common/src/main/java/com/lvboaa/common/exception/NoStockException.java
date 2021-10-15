package com.lvboaa.common.exception;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/10/11 15:08
 */
public class NoStockException extends RuntimeException {

    private Long skuId;

    public NoStockException(){
        super();
    }

    public NoStockException(Long skuId){
        super("商品id："+skuId+"，没有足够的库存了");
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getSkuId() {
        return skuId;
    }
}
