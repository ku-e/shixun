package com.zjc.item.mapper;

import com.zjc.shop.pojo.Sku;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuMapper extends Mapper<Sku>, SelectByIdListMapper<Sku,Long> {
    @Select("select * from tb_sku where spu_id=#{id}")
    List<Sku> findSkuBySpuId(Long id);

}
