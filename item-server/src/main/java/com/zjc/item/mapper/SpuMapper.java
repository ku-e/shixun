package com.zjc.item.mapper;

import com.zjc.shop.pojo.Spu;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface SpuMapper extends Mapper<Spu> {
    //下架
    @Update("UPDATE tb_spu set saleable=0 WHERE id=#{id}")
    void xiajia(Long id);

    //上架
    @Update("UPDATE tb_spu set saleable=1 WHERE id=#{id}")
    void shangjia(Long id);
}
