package com.zjc.order.mapper;

import com.zjc.order.pojo.OrderDetail;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

@Component
public interface OrderDetailMapper extends Mapper<OrderDetail>, InsertListMapper<OrderDetail> {
}
