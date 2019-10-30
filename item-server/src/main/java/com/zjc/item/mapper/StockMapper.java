package com.zjc.item.mapper;

import com.zjc.shop.pojo.Stock;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface StockMapper extends InsertListMapper<Stock>, Mapper<Stock> {
}
