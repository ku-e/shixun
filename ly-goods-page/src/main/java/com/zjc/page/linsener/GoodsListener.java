package com.zjc.page.linsener;

import com.zjc.page.service.FileService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {

    @Autowired
    FileService fileService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ly.rrrr",durable = "true"),
            exchange = @Exchange(
                    value = "ly.item.exchange2",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.update2"}))
    public void listenCreate(Long id) throws Exception {
        System.out.println("id::::::::::::"+id);
        fileService.createHtml(id);
    }


}
