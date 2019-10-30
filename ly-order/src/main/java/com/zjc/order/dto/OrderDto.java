package com.zjc.order.dto;

import com.zjc.shop.dto.CartDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    @NotNull
    private Long addressId;//收货人地址id
    @NotNull
    private Integer paymentType;//付款类型
    @NotNull
    private List<CartDto> orderDetails;//订单详情


}
