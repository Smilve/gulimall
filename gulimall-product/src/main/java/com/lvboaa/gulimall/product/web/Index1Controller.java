package com.lvboaa.gulimall.product.web;

import com.lvboaa.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/9/15 17:21
 */
@Controller
public class Index1Controller {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){
       model.addAttribute("categories",categoryService.getLevel1Category());
       return "index";
    }

    @GetMapping("/index/catalog.json")
    @ResponseBody
    public Object listTree(){
        return categoryService.getCatalogJson();
    }

}
