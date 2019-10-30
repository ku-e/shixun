package com.zjc.cart.client;

import com.zjc.shop.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-server")
public interface GoodsClient extends GoodsApi {
}
