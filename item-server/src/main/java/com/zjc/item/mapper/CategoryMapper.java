package com.zjc.item.mapper;

import com.zjc.shop.pojo.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category, Long>, IdListMapper<Category, Long> {

    @Select("select * from tb_category where parent_id = #{parenId}")
    public List<Category> findByParenId(@RequestParam("parenId") Long parenId);

    @Delete("delete from tb_category where id = #{id}")
    void deleteById(@RequestParam("id") Long id);

    /**
     * 根据品牌id查询商品分类
     * @param bid
     * @return
     */
    @Select("SELECT * FROM tb_category WHERE id IN (SELECT category_id FROM tb_category_brand WHERE brand_id = #{bid})")
    List<Category> queryByBrandId(Long bid);
}
