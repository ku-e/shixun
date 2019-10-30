package com.zjc.page.client;

import com.zjc.shop.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-server")
public interface BrandClient extends BrandApi {
}
