package com.zjc.search.api;

import com.zjc.shop.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-server")
public interface GoodsClient extends GoodsApi {
}
