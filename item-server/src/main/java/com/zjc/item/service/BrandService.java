package com.zjc.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zjc.common.pojo.PageResult;
import com.zjc.item.mapper.BrandMapper;
import com.zjc.shop.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPageAndSort(
            Integer page,
            Integer rows,
            String sortBy,
            Boolean desc,
            String key){
        //开始分页
        PageHelper.startPage(page,rows);
        //过滤
        Example example = new Example(Brand.class);
        if (key!=null&&!key.equals("")) {
            example.createCriteria().andLike("name", "%" + key + "%").orEqualTo("letter", key.toUpperCase());
        }
        if (sortBy!=null&&!sortBy.equals("")){
            // 排序
            String orderByClause = sortBy+(desc ? " DESC":" ASC");
            example.setOrderByClause(orderByClause);
        }
        //查询
        Page<Brand> pageInfo = (Page<Brand>) brandMapper.selectByExample(example);
        //返回结果
        return new PageResult<>(pageInfo.getTotal(),pageInfo);
    }

    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //新增品牌信息
        this.brandMapper.insertSelective(brand);
        //新增品牌和分类中间表
        for (Long cid : cids) {
            System.out.println(cid);
            System.out.println(brand.getId());
            this.brandMapper.insertCategoryBrand(cid,brand.getId());
        }
    }

    public void updateBrand(Brand brand, List<Long> cids) {
        Long id = brand.getId();
        //修改品牌信息
        brandMapper.deleteById(id);
        this.brandMapper.updateByPrimaryKey(brand);
        //修改品牌和分类的中间表
        for (Long cid : cids) {
            System.out.println(cid);
            System.out.println(brand.getId());
            this.brandMapper.insertCategoryBrand(cid,brand.getId());
        }
    }

    public void del(Long id) {
        brandMapper.deleteById(id);
        brandMapper.deleteByPrimaryKey(id);
    }

    public Brand queryById(Long brandId) {
        return brandMapper.selectByPrimaryKey(brandId);
    }

    public List<Brand> queryBrandByCategory(Long cid) {
        return brandMapper.queryByCategoryId(cid);
    }

    public List<Brand> queryBrandByIds(List<Long> ids) {
        return brandMapper.selectByIdList(ids);
    }
}
