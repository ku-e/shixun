
package com.zjc.item.mapper;

import com.zjc.shop.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand>, SelectByIdListMapper<Brand,Long> {

    /**
     * 新增商品分类和品牌中间表数据
     * @param cid  商品分类id
     * @param bid  品牌id
     * @return
     */
    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
    void insertCategoryBrand(@Param("cid")Long cid, @Param("bid")Long bid);

    @Delete("delete from tb_category_brand where brand_id=#{id}")
    void deleteById(@Param("id") Long id);

    @Select("select b.* from tb_category_brand cb left join tb_brand b on cb.brand_id = b.id where cb.category_id = #{cid}")
    List<Brand> queryByCategoryId(Long cid);
}