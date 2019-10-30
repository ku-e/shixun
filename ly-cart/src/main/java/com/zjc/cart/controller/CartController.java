package com.zjc.cart.controller;

import com.zjc.cart.service.CartService;
import com.zjc.shop.pojo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加购物车
     * @param token
     * @param cart
     * @return
     */
    @PostMapping("/addCart")
    public ResponseEntity<Void> addCart(@CookieValue("LY_TOKEN") String token,@RequestBody Cart cart){
        cartService.addCart(token,cart);
        return ResponseEntity.ok().build();
    }

    /**
     * 查询购物车
     * @param token
     * @return
     */
    @GetMapping("listCart")
    public ResponseEntity<List<Cart>> queryCartList(@CookieValue("LY_TOKEN") String token){
        List<Cart> carts = cartService.queryCartList(token);
        if (carts == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(carts);
    }

    /**
     * 修改购物车
     * @param skuId
     * @param num
     * @param token
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateNum(@RequestParam("skuId")Long skuId,
                                          @RequestParam("num")Integer num,
                                          @CookieValue("LY_TOKEN") String token){
        cartService.updateNum(skuId,num,token);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除购物车商品
     * @param skuId
     * @param token
     * @return
     */
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId")String skuId,@CookieValue("LY_TOKEN")String token){
        cartService.deleteCart(skuId,token);
        return ResponseEntity.ok().build();
    }

    /**
     * 登录后购物合并
     * @param list
     * @param token
     * @return
     */
    @PostMapping("LocalStorage")
    public ResponseEntity<Void> LocalStorage(@RequestBody List<Cart> list,
                                             @CookieValue("LY_TOKEN")String token){
        cartService.LocalStorage(list,token);
        return ResponseEntity.ok().build();
    }
}
