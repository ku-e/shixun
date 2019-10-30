package com.zjc.item.controller;

import com.zjc.common.pojo.PageResult;
import com.zjc.item.service.GoodsService;
import com.zjc.shop.dto.CartDto;
import com.zjc.shop.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    @RequestMapping("spu/page")
    public ResponseEntity<PageResult<Spu>> queryByPage(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable
    ){
        return ResponseEntity.ok(goodsService.queryByPage(page,rows,key,saleable));

    }


    /**
     * 新增商品
     *@param spu
     * @return
     */
    @PostMapping("goods") // json对象加上@RequestBody
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu) {
        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("spu/detail/{id}")
    public ResponseEntity<SpuDetail> querySpuDetailById(@PathVariable("id")Long id){
        SpuDetail detail = goodsService.querySpuDetailById(id);
        if (detail == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(detail);

    }

    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id")Long id){
        List<Sku> skus = goodsService.querySkuBySpuId(id);
        if (skus == null || skus.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(skus);
    }

    /**
     * 修改
     * @param spu
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spu){
        try{
            goodsService.update(spu);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 下架
     * @param id
     * @return
     */
    @GetMapping("/spu/xiajia/{id}")
    public boolean xiajia(@PathVariable("id")Long id){
        try {
            goodsService.xiajia(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 上架
     * @param id
     * @return
     */
    @GetMapping("/spu/shangjia/{id}")
    public boolean shangjia(@PathVariable("id")Long id){
        try {
            goodsService.shangjia(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id")Long id){
        Spu spu = goodsService.querySpuById(id);
        System.out.println("spu====="+spu);
        if (spu == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(spu);
    }


    @GetMapping("/querySkuById")
    ResponseEntity<Sku> querySkuById(@RequestParam("skuId") Long skuId){
        Sku sku = goodsService.querySkuById(skuId);
        return  ResponseEntity.ok(sku);
    }

    @RequestMapping("/queryskusByIds")
    public ResponseEntity<List<Sku>> queryskusByIds( @RequestBody List<Long> longs){
        List<Sku> skus = goodsService.queryskusByIds(longs);
        return ResponseEntity.ok(skus);
    }

    @RequestMapping("/queryStockBySkuId")
    public ResponseEntity<Stock> queryStockBySkuId(@RequestParam("id")Long id){
        Stock stock = goodsService.queryStockBySkuId(id);
        return ResponseEntity.ok(stock);
    }

    @RequestMapping("/decreaseStock")
    public ResponseEntity<Boolean> decreaseStock(@RequestBody List<CartDto> orderDetails){
        Boolean aBoolean = goodsService.decreaseStock(orderDetails);
        return ResponseEntity.ok(aBoolean);
    }


}
