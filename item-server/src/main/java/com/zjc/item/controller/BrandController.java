package com.zjc.item.controller;

import com.zjc.common.pojo.PageResult;
import com.zjc.item.service.BrandService;
import com.zjc.shop.pojo.Brand;
import com.zjc.shop.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy",required = false)String sortBy,
            @RequestParam(value = "desc",defaultValue = "false")Boolean desc,
            @RequestParam(value = "key",required = false)String key){
        PageResult<Brand> result = this.brandService.queryBrandByPageAndSort(page,rows,sortBy,desc,key);
        if (result == null || result.getItems().size() == 0){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增品牌
     * @param  brand
     * @return
     */

    @RequestMapping("/saveBrand")
    public ResponseEntity<Void>saveBrand(Brand brand, @RequestParam("cids")List<Long> cids){
        this.brandService.saveBrand(brand,cids);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //修改
    @PutMapping("/saveBrand")
    public ResponseEntity<Void>updateBrand(Brand brand, @RequestParam("cids")List<Long> cids){
        this.brandService.updateBrand(brand,cids);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //删除
    @GetMapping("/del")
    public boolean del(@RequestParam("id") Long id){
        System.out.println(id);
        try{
            brandService.del(id);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据分类查询品牌
     * @param cid
     * @return
     */
    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCategory(@PathVariable("cid")Long cid){
        List<Brand> list = this.brandService.queryBrandByCategory(cid);
        if (list == null){
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);

    }

    @GetMapping("/{id}")
    public Brand queryBrandById(@PathVariable("id") Long id){
        return brandService.queryById(id);
    }

    /**
     * 根据多个id查询品牌
     * @param ids
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids") List<Long> ids){
        List<Brand> list = brandService.queryBrandByIds(ids);
        if (list == null){
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }



}
