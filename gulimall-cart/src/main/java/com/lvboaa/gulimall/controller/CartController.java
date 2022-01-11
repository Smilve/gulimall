package com.lvboaa.gulimall.controller;

import com.lvboaa.gulimall.interceptor.CartInterceptor;
import com.lvboaa.gulimall.service.CartService;
import com.lvboaa.gulimall.vo.CartItemVo;
import com.lvboaa.gulimall.vo.CartVo;
import com.lvboaa.gulimall.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Description:
 *
 *
 * @author lv.bo
 * @date 2021/9/28 10:02
 */
@Controller
public class CartController {

    @Autowired
    CartService cartService;

    /**
     * 浏览器有一个cookie:user-key标识未登录的身份，一个月后过期
     * 第一次使用购物车功能，都会给一个user-key临时身份，每次请求都会带上这个cookie
     *
     * 登录：session有
     * 没登录：按照cookie的user-key来做，第一次登录没有user-key，需要创建一个user-key(临时用户)：拦截器
     * @param model
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {

        /**
         * 快速得到用户信息
         * ThreadLocal 同一个线程共享数据
         */
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        CartVo cart = cartService.getCart();
        model.addAttribute("cart",cart);
        return "cartList";
    }

    /**
     * 添加商品到购物车
     * attributes.addFlashAttribute():将数据放在session中，可以在页面中取出，但是只能取一次
     * attributes.addAttribute():将数据放在url后面 ?skuId=skuId
     * 重定向到addToCart.html：防止用户重刷，一直添加商品 ，再刷新也只是查询数据
     * @return
     */
    @GetMapping(value = "/addCartItem")
    public String addCartItem(@RequestParam("skuId") Long skuId,
                              @RequestParam("num") Integer num,
                              RedirectAttributes attributes) throws ExecutionException, InterruptedException {

        cartService.addToCart(skuId,num);

        //attributes.addAttribute("skuId",skuId);
        return "redirect:http://cart.gulimall.com/addToCart.html?skuId="+skuId;
    }


    /**
     * 跳转到添加购物车成功页面
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/addToCart.html")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            Model model){

        // 重定向到成功页面，并查询购物车数据
        CartItemVo cartItem = cartService.getCartItem(skuId);
        model.addAttribute("cartItem",cartItem);
        return "success";
    }

    /**
     * 改变商品数量
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping(value = "/countItem")
    public String countItem(@RequestParam(value = "skuId") Long skuId,
                            @RequestParam(value = "num") Integer num) {

        cartService.changeItemCount(skuId,num);

        return "redirect:http://cart.gulimall.com/cart.html";
    }


    /**
     * 删除商品信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/deleteItem")
    public String deleteItem(@RequestParam("skuId") Integer skuId) {

        cartService.deleteIdCartInfo(skuId);

        return "redirect:http://cart.gulimall.com/cart.html";

    }

    /**
     * 商品是否选中
     * @param skuId
     * @param checked
     * @return
     */
    @GetMapping(value = "/checkItem")
    public String checkItem(@RequestParam(value = "skuId") Long skuId,
                            @RequestParam(value = "checked") Integer checked) {

        cartService.checkItem(skuId,checked);

        return "redirect:http://cart.gulimall.com/cart.html";

    }

    /**
     * 获取当前用户的购物车商品项
     * @return
     */
    @GetMapping(value = "/currentUserCartItems")
    @ResponseBody
    public List<CartItemVo> getCurrentCartItems() {

        return cartService.getUserCartItems();
    }

}
