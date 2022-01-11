package com.lvboaa.search.vo;

import lombok.Data;

import java.util.List;

/**
 * Description: 封装页面所有可能传递过来的条件
 *
 * @author lv.bo
 * @date 2021/9/16 11:36
 */
@Data
public class SearchParam {

    private String keyword; // 页面传递过来的全文检索关键字
    private Long catalog3Id; // 三级分类id

    /**
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hotPrice_asc/desc
     */
    private String sort; // 排序条件

    /**
     * hasStock=0/1 是否有货
     * skuPrice=1_500/_500/500_ 价格区间
     * brandId
     * catalog3Id
     * attrs=1_安卓:苹果
     */
    private Integer hasStock; // 是否有货
    private String skuPrice; // 价格区间
    private List<Long> brandId; // 按照品牌id查询，可以多选
    private List<String> attrs; //属性筛选
    private Integer pageNum; // 页码

    /**
     * 原生的所有查询条件
     */
    private String _queryString;
}
