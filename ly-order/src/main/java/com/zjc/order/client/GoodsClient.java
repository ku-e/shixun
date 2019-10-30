package com.zjc.order.client;

import com.zjc.shop.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@FeignClient(value = "item-server")
@Component
public interface GoodsClient extends GoodsApi {
}
