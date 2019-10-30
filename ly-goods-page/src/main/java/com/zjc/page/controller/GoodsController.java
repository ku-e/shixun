package com.zjc.page.controller;

import com.zjc.page.service.FileService;
import com.zjc.page.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/item")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private FileService fileService;
    /**
     * 跳转到商品详情页
     * @param model
     * @param id
     * @return
     */
    @GetMapping("{id}.html")
    public String toItemPage(Model model, @PathVariable("id")Long id){
        //加载所需数据
        Map<String, Object> modelMap = goodsService.loadModel(id);
        //放入模型
        model.addAllAttributes(modelMap);
        //判断是否需要生成新的页面
        if (!fileService.exists(id)){
            fileService.syncCreateHtml(id);
        }
        return "item";
    }
}
