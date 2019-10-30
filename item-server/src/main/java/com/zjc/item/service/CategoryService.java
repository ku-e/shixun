package com.zjc.item.service;

import com.zjc.common.enums.ExceptionEnum;
import com.zjc.common.exception.LyException;
import com.zjc.item.mapper.CategoryMapper;
import com.zjc.shop.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    public List<Category> queryListByPerent(Long pid){
        return categoryMapper.findByParenId(pid);
    }

    public void deleteById(Long id) {
        categoryMapper.deleteById(id);
    }


    public List<Category> queryByBrandId(Long bid) {
        return this.categoryMapper.queryByBrandId(bid);
    }

    public List<Category> queryByIds(List<Long> ids) {
        final List<Category> list = categoryMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return list;
    }

    /**
     * 根据分类ID查询出分类名称
     * @param ids
     * @return
     */
    public List<String> queryNameByIds(List<Long> ids) {
        List<Category> categories = this.categoryMapper.selectByIdList(ids);
        Stream<Category> stream = categories.stream();
        List<String> names = stream.map(Category::getName).collect(Collectors.toList());
        return names;
    }

    public List<Category> queryCategoryListByids(List<Long> ids) {
        List<Category> list = categoryMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return list;
    }
}
