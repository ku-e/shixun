package com.zjc.item.controller;

import com.sun.org.apache.regexp.internal.RE;
import com.zjc.common.exception.LyException;
import com.zjc.item.service.SpecificationService;
import com.zjc.shop.pojo.SpecGroup;
import com.zjc.shop.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    SpecificationService specificationService;

    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroups(@PathVariable("cid")Long cid){
        List<SpecGroup> list = this.specificationService.querySpecGroups(cid);
        if (list == null || list.size() == 0){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }
    //添加
    @PostMapping("group")
    public ResponseEntity<Void> svaeGroup(@RequestBody SpecGroup specGroup){
        System.out.println(specGroup+"gfhgfhgfhfghgfh");
        this.specificationService.saveGroup(specGroup);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //修改规格组
    @PutMapping("group")
    public ResponseEntity<Void> updateGroup(@RequestBody SpecGroup specGroup){
        System.out.println(specGroup+"添加参数");
        this.specificationService.updataGroup(specGroup);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //删除规格组
    @DeleteMapping("group/{id}")
    public void del(@PathVariable("id")Long id){
        System.out.println(id+"oooooooooooooooo");
        this.specificationService.del(id);
    }


    //规格参数
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParam(
            @RequestParam(value = "gid",required = false)Long gid,
            @RequestParam(value="cid", required = false) Long cid,
            @RequestParam(value="searching", required = false) Boolean searching,
            @RequestParam(value="generic", required = false) Boolean generic){
        List<SpecParam> list = this.specificationService.querySpecParam(gid,cid,searching,generic);
        if (list == null || list.size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    //添加规格参数
    @PostMapping("param")
    public ResponseEntity<Void> svaeParam(@RequestBody SpecParam specParam){
        System.out.println(specParam+"gfhgfhgfhfghgfh");
        this.specificationService.saveParam(specParam);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //修改规格参数
    @PutMapping("param")
    public ResponseEntity<Void> updateParam(@RequestBody SpecParam specParam){
        System.out.println(specParam+"gfhgfhgfhfghgfh");
        this.specificationService.updateParam(specParam);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //删除规格组
    @DeleteMapping("param/{id}")
    public void delByParam(@PathVariable("id")Long id){
        System.out.println(id+"oooooooooooooooo");
        this.specificationService.delByParam(id);
    }

    @GetMapping("{id}")
    public ResponseEntity<List<SpecGroup>> querySpecsByCid(@PathVariable("cid")Long cid){
        List<SpecGroup> list = specificationService.querySpecsByCid(cid);
        if (list == null || list.size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }
}
