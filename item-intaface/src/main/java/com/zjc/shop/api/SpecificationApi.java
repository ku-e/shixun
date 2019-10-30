package com.zjc.shop.api;

import com.zjc.shop.pojo.SpecGroup;
import com.zjc.shop.pojo.SpecParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("spec")
public interface SpecificationApi {

    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroups(@PathVariable("cid")Long cid);

    //添加
    @PostMapping("group")
    public ResponseEntity<Void> svaeGroup(@RequestBody SpecGroup specGroup);

    //修改规格组
    @PutMapping("group")
    public ResponseEntity<Void> updateGroup(@RequestBody SpecGroup specGroup);

    //删除规格组
    @DeleteMapping("group/{id}")
    public void del(@PathVariable("id")Long id);


    //规格参数
    @GetMapping("/params")
    public List<SpecParam> querySpecParam(
            @RequestParam(value = "gid",required = false)Long gid,
            @RequestParam(value="cid", required = false) Long cid,
            @RequestParam(value="searching", required = false) Boolean searching,
            @RequestParam(value="generic", required = false) Boolean generic);

    //添加规格参数
    @PostMapping("param")
    public ResponseEntity<Void> svaeParam(@RequestBody SpecParam specParam);

    //修改规格参数
    @PutMapping("param")
    public ResponseEntity<Void> updateParam(@RequestBody SpecParam specParam);

    //删除规格组
    @DeleteMapping("param/{id}")
    public void delByParam(@PathVariable("id")Long id);

    //查询规格参数组，及组内参数
    @GetMapping("{id}")
    List<SpecGroup> querySpecsByCid(@PathVariable("cid")Long cid);
}
