package com.zjc.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private Long skuId;  //商品skuId
    private Integer num;  //购买数量
    private String title;
    private String image;
}
