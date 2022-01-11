package com.lvboaa.search.service;

import com.lvboaa.search.vo.SearchParam;
import com.lvboaa.search.vo.SearchResult;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/9/16 11:35
 */
public interface MallSearchService {
    /**
     *
     * @param searchParam   检索的所有参数
     * @return  检索的结果
     * @author lv.bo
     * @date 2021/9/16 11:37
     */
    SearchResult search(SearchParam searchParam);
}
