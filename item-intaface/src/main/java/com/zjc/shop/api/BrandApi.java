package com.zjc.shop.api;

import com.zjc.common.pojo.PageResult;
import com.zjc.shop.pojo.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/brand")
public interface BrandApi {

    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy",required = false)String sortBy,
            @RequestParam(value = "desc",defaultValue = "false")Boolean desc,
            @RequestParam(value = "key",required = false)String key);

    /**
     * 新增品牌
     * @param  brand
     * @return
     */

    @RequestMapping("/saveBrand")
    public ResponseEntity<Void>saveBrand(Brand brand, @RequestParam("cids") List<Long> cids);

    //修改
    @PutMapping("/saveBrand")
    public ResponseEntity<Void>updateBrand(Brand brand, @RequestParam("cids")List<Long> cids);

    //删除
    @GetMapping("/del")
    public boolean del(@RequestParam("id") Long id);

    /**
     * 根据分类查询品牌
     * @param cid
     * @return
     */
    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCategory(@PathVariable("cid")Long cid);

    @GetMapping("/{id}")
    public Brand queryBrandById(@PathVariable("id") Long id);

    /**
     * 根据多个id查询品牌
     * @param ids
     * @return
     */
    @GetMapping("/list")
    public List<Brand> queryBrandByIds(@RequestParam("ids") List<Long> ids);
}
