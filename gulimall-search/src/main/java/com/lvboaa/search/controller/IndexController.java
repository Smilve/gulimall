package com.lvboaa.search.controller;

import com.lvboaa.search.service.MallSearchService;
import com.lvboaa.search.vo.SearchParam;
import com.lvboaa.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/9/16 10:42
 */
@Controller
public class IndexController {

    @Autowired
    MallSearchService mallSearchService;

    @RequestMapping({"/","/list.html","/index"})
    public String list(SearchParam searchParam, Model model, HttpServletRequest request){
        searchParam.set_queryString(request.getQueryString());
        SearchResult result = mallSearchService.search(searchParam);
        model.addAttribute("result",result);
        return "list";
    }
}
