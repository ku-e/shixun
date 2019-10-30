package com.zjc.page.client;

import com.zjc.shop.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-server")
public interface GoodsClient extends GoodsApi {
}
