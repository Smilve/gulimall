package com.lvboaa.search.vo;

import com.lvboaa.common.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/9/17 11:23
 */
@Data
public class SearchResult {

    // 查询到所有的商品信息
    private List<SkuEsModel> product;

    private Integer pageNum; // 当前页码
    private Long total; // 总记录数
    private Integer totalPages; // 总页数

    private List<Integer> pageNavs;

    private List<BrandVo> brands; // 所有查询到的品牌
    private List<CatalogVo> catalogs; // 结果涉及分类
    private List<AttrVo> attrs; // 当前查询结果所涉及的属性

    /* 面包屑导航数据 */
    private List<NavVo> navs;

    @Data
    public static class NavVo {
        private String navName;
        private String navValue;
        private String link;
    }

    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
}
