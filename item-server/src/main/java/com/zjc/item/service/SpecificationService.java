package com.zjc.item.service;

import com.zjc.item.mapper.SpecGroupMapper;
import com.zjc.item.mapper.SpecParamMapper;
import com.zjc.shop.pojo.SpecGroup;
import com.zjc.shop.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationService {

    @Autowired
    SpecGroupMapper specGroupMapper;
    @Autowired
    SpecParamMapper specParamMapper;


    public List<SpecGroup> querySpecGroups(Long cid) {
        SpecGroup group = new SpecGroup();
        group.setCid(cid);
        return this.specGroupMapper.select(group);
    }


    public List<SpecParam> querySpecParam(Long gid,Long cid, Boolean searching, Boolean generic) {
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        param.setCid(cid);
        param.setSearching(searching);
        param.setGeneric(generic);
        return this.specParamMapper.select(param);
    }

    public void saveGroup(SpecGroup specGroup) {
        //新增规格组信息
        this.specGroupMapper.insertSelective(specGroup);
    }

    public void updataGroup(SpecGroup specGroup) {
        //修改规格组信息
        this.specGroupMapper.updateByPrimaryKeySelective(specGroup);
    }

    public void del(Long id) {
        this.specGroupMapper.deleteByPrimaryKey(id);
    }

    public void saveParam(SpecParam specParam) {
        this.specParamMapper.insertSelective(specParam);
    }

    public void updateParam(SpecParam specParam) {
        this.specParamMapper.updateByPrimaryKeySelective(specParam);
    }

    public void delByParam(Long id) {
        this.specParamMapper.deleteByPrimaryKey(id);
    }

    public List<SpecGroup> querySpecsByCid(Long cid) {
        //查询规格组
        List<SpecGroup> groups = querySpecGroups(cid);
        SpecParam param = new SpecParam();
        groups.forEach(g -> {
            //查询组内参数
            g.setParams(querySpecParam(g.getId(),null,null,null));
        });
        return groups;


    }
}
