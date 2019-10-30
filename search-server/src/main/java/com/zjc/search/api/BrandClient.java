package com.zjc.search.api;

import com.zjc.shop.api.BrandApi;
import com.zjc.shop.pojo.Brand;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-server")
public interface BrandClient extends BrandApi {
}
