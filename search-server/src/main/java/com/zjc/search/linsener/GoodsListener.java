package com.zjc.search.linsener;


import com.zjc.search.api.GoodsClient;
import com.zjc.search.dao.GoodsRepository;
import com.zjc.search.pojo.Goods;
import com.zjc.search.service.IndexService;
import com.zjc.shop.pojo.Spu;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GoodsListener {

    @Autowired
    IndexService indexService;
    @Autowired
    GoodsClient goodsClient;
    @Autowired
    GoodsRepository goodsRepository;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ly.wert",durable = "true"),
            exchange = @Exchange(
                    value = "ly.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.insert","item.update"}))
    public void listenter(Long spuId){

        System.out.println("哈哈哈哈哈哈哈哈哈哈哈：哈哈哈哈哈哈哈哈哈哈");
        Optional<Goods> byId = goodsRepository.findById(spuId);
        System.out.println("监听到的id为："+spuId);
        if (byId != null){
            goodsRepository.deleteById(spuId);
        }

        ResponseEntity<Spu> spuResponseEntity = goodsClient.querySpuById(spuId);
        Goods goods = indexService.buildGoods(spuResponseEntity.getBody());
        goodsRepository.save(goods);



    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ly.queue",durable = "true"),
            exchange = @Exchange(
                    value = "ly.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.delete"}))
    public void delete(Long spuId){
        indexService.delete(spuId);
    }
}
