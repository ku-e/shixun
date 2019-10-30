package com.zjc.order.mapper;

import com.zjc.order.pojo.Order;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

@Component
public interface OrderMapper extends Mapper<Order> {
}
