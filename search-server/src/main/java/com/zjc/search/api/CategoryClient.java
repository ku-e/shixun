package com.zjc.search.api;

import com.zjc.shop.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-server")
public interface CategoryClient extends CategoryApi {
}
