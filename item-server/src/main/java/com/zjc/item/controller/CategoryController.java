package com.zjc.item.controller;

import com.zjc.common.enums.ExceptionEnum;
import com.zjc.common.exception.LyException;
import com.zjc.item.service.CategoryService;
import com.zjc.shop.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("list")
    public List<Category> queryByParentId(
            @RequestParam(value = "pid",defaultValue = "0")Long pid){
        List<Category> list = this.categoryService.queryListByPerent(pid);
        if (list != null && list.isEmpty()){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return list;

    }

    @GetMapping("/deleteById")
    public boolean deleteById(Long id){
        System.out.println(id);
        try{
            categoryService.deleteById(id);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 通过品牌id查询商品分类
     * @param bid
     * @return
     */
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid") Long bid){
        System.out.println(bid);
        List<Category> list = this.categoryService.queryByBrandId(bid);
        if (list == null || list.size() < 1){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 根据商品分类id查询名称
     * @param ids 要查询的分类id集合
     * @return 多个名称的集合
     */
    @GetMapping("names")
    public ResponseEntity<List<String>> queryNameByIds(@RequestParam("ids")List<Long> ids){
        List<String> list = categoryService.queryNameByIds(ids);
        if (list == null || list.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    @RequestMapping("clist")
    public ResponseEntity<List<Category>> queryCategoryListByids(@RequestParam("ids") List<Long> ids){
        List<Category> list = categoryService.queryCategoryListByids(ids);
        return ResponseEntity.ok(list);
    }
}
