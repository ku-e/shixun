package com.zjc.page.client;

import com.zjc.shop.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-server")
public interface SpecificationClient extends SpecificationApi {
}
