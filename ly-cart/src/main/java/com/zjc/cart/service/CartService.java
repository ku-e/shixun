package com.zjc.cart.service;


import com.zjc.cart.client.GoodsClient;
import com.zjc.common.util.JsonUtils;
import com.zjc.shop.pojo.Cart;
import com.zjc.shop.pojo.Sku;
import com.zjc.shop.pojo.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GoodsClient goodsClient;

    static final String KEY_PREFIX = "ly:cart:uid:";

    static final Logger logg = LoggerFactory.getLogger(CartService.class);

    public void addCart(String token ,Cart cart) {

        //根据token获取登录用户
        User user = JsonUtils.toBean(redisTemplate.opsForValue().get(token), User.class);
        //Redis的key
        String key = KEY_PREFIX + user.getId();
        //获取hash操作对象
        BoundHashOperations<String,Object,Object> hashOps = redisTemplate.boundHashOps(key);
        //查询是否存在
        Long skuId = cart.getSkuId();
        Integer num = cart.getNum();
        Boolean boo = hashOps.hasKey(skuId.toString());
        if (boo){
            //存在，获取购物车数据
            String json = hashOps.get(skuId.toString()).toString();
            cart = JsonUtils.toBean(json, Cart.class);
            //修改购物车
            cart.setNum(cart.getNum() + num);
        }else {
            //不存在新增购物车数据
            cart.setUserId(user.getId());
            // 其它商品信息， 需要查询商品服务
            ResponseEntity<Sku> resp = this.goodsClient.querySkuById(skuId);
            if (resp.getStatusCode() != HttpStatus.OK || !resp.hasBody()){
                logg.error("添加购物车的商品不存在：skuId:{}", skuId);
                throw new RuntimeException();
            }
            Sku sku = resp.getBody();
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            cart.setPrice(sku.getPrice());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
        }
        // 将购物车数据写入redis
        System.out.println("key==="+key);
        hashOps.put(cart.getSkuId().toString(), JsonUtils.toString(cart));
    }

    public List<Cart> queryCartList(String token) {
        User user = JsonUtils.toBean(redisTemplate.opsForValue().get(token), User.class);
        //判断是否在购物车
        String key = KEY_PREFIX + user.getId();
        if(!redisTemplate.hasKey(key)){
            //不存在直接返回
            return null;
        }
        BoundHashOperations<String,Object,Object> hashOps = redisTemplate.boundHashOps(key);
        List<Object> carts = hashOps.values();
        if (CollectionUtils.isEmpty(carts)){
            return null;
        }
        // 查询购物车数据
        return carts.stream().map(o -> JsonUtils.toBean(o.toString(), Cart.class)).collect(Collectors.toList());
    }

    public void updateNum(Long skuId, Integer num, String token) {
        User user = JsonUtils.toBean(redisTemplate.opsForValue().get(token), User.class);
        String key = KEY_PREFIX + user.getId();
        BoundHashOperations<String,Object,Object> hashOps = redisTemplate.boundHashOps(key);
        //获取购物车
        String json = hashOps.get(skuId.toString()).toString();
        Cart cart = JsonUtils.toBean(json, Cart.class);
        cart.setNum(num);
        //写入购物车
        hashOps.put(skuId.toString(),JsonUtils.toString(cart));


    }

    public void deleteCart(String skuId, String token) {
        User user = JsonUtils.toBean(redisTemplate.opsForValue().get(token), User.class);
        String key = KEY_PREFIX + user.getId();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        hashOps.delete(skuId);
    }

    public void LocalStorage(List<Cart> list, String token) {
        for (Cart cart : list) {
            addCart(token,cart);
        }
    }
}
