package com.zjc.shop.api;

import com.zjc.common.pojo.PageResult;
import com.zjc.shop.dto.CartDto;
import com.zjc.shop.pojo.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

public interface GoodsApi {

    @RequestMapping("spu/page")
    public ResponseEntity<PageResult<Spu>> queryByPage(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable);


    /**
     * 新增商品
     *@param spu
     * @return
     */
    @PostMapping("goods") // json对象加上@RequestBody
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu);

    @GetMapping("spu/detail/{id}")
    public ResponseEntity<SpuDetail> querySpuDetailById(@PathVariable("id")Long id);

    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id")Long id);

    /**
     * 修改
     * @param spu
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spu);

    /**
     * 下架
     * @param id
     * @return
     */
    @GetMapping("/spu/xiajia/{id}")
    public boolean xiajia(@PathVariable("id")Long id);

    /**
     * 上架
     * @param id
     * @return
     */
    @GetMapping("/spu/shangjia/{id}")
    public boolean shangjia(@PathVariable("id")Long id);

    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    ResponseEntity<Spu> querySpuById(@PathVariable("id")Long id);

    @GetMapping("/querySkuById")
    ResponseEntity<Sku> querySkuById(@RequestParam("skuId") Long skuId);

    @RequestMapping("/queryskusByIds")
    List<Sku> queryskusByIds(@RequestBody List<Long> longs);

    @RequestMapping("/queryStockBySkuId")
    Stock queryStockBySkuId(@RequestParam("id") Long id);

    @RequestMapping("/decreaseStock")
    void decreaseStock(@RequestBody List<CartDto> orderDetails);
}
