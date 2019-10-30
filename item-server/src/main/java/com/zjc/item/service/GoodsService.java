package com.zjc.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjc.common.enums.ExceptionEnum;
import com.zjc.common.exception.LyException;
import com.zjc.common.pojo.PageResult;
import com.zjc.item.mapper.*;
import com.zjc.shop.dto.CartDto;
import com.zjc.shop.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    private static final Logger log = LoggerFactory.getLogger(GoodsService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public PageResult<Spu> queryByPage(Integer page, Integer rows, String key, Boolean saleable) {
        //分页
        PageHelper.startPage(page,rows);

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        if (saleable != null) {
            criteria.orEqualTo("saleable", saleable);
        }
        //默认以上一次更新时间排序
        example.setOrderByClause("last_update_time desc");

        //只查询未删除的商品
        criteria.andEqualTo("valid", 1);

        //查询
        List<Spu> spuList = spuMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(spuList)) {
            throw new LyException(ExceptionEnum.SPU_NOT_FOUND);
        }
        //对查询结果中的分类名和品牌名进行处理
        loadCategoryAndBrandName(spuList);

        PageInfo<Spu> pageInfo = new PageInfo<>(spuList);

        return new PageResult<>(pageInfo.getTotal(), spuList);
    }

    private void loadCategoryAndBrandName(List<Spu> spus){
        for (Spu spu : spus) {
            //1.查询分类
            List<Category> categories = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            //2.将分类集合转化成分类到流数据
            Stream<Category> stream = categories.stream();
            //3.获取分类名称流数据
            Stream<String> stringStream = stream.map(c -> c.getName());
            //4.将名称流数据转化成分类名称集合
            List<String> names = stringStream.collect(Collectors.toList());
            //分类名称转化成分割到字符串
            spu.setCname(StringUtils.join(names,"/"));
            //查询品牌数据
            Brand brand = brandService.queryById(spu.getBrandId());
            spu.setBname(brand.getName());

        }
    }


    //添加
    @Transactional
    public void saveGoods(Spu spu) {
        // 新增spu
        saveSpu(spu);
        // 新增detail
        saveSpuDetail(spu);
        // 新增sku和库存
        saveSkuAndStock(spu);

        //发送消息
        sendMessage(spu.getId(),"insert");
        System.out.println("添加监听到id:"+spu.getId());
    }

    //封装一个发送消息方法
    private void sendMessage(Long id,String type){
        //发送消息
        try {
            rabbitTemplate.convertAndSend("ly.item.exchange","item."+type,id);

            System.out.println("sssssssss:::sssssssss");
        }catch (Exception e){
            log.error("{}商品消息发送异常，商品id:{}",type,id,e);
        }
    }

    private void  saveSkuAndStock(Spu spu){
        List<Stock> list = new ArrayList<>();
        List<Sku> skus = spu.getSkus();
        for (Sku sku : skus) {
            // 保存sku
            sku.setSpuId(spu.getId());
            // 初始化时间
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            int count = skuMapper.insert(sku);
            if (count != 1) {
                throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }
            // 保存库存信息
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            list.add(stock);
        }
        System.out.println(skus);
        // 批量新增库存
//        int count = stockMapper.insertList(list);
//        if (count != 1) {
//            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
//        }
        for (Stock stock : list) {
            stockMapper.insert(stock);
        }
        System.out.println(list);
    }

    private void saveSpuDetail(Spu spu){
        SpuDetail detail = spu.getSpuDetail();
        detail.setSpuId(spu.getId());
        int count = spuDetailMapper.insert(detail);
        if (count != 1){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
    }

    private void saveSpu(Spu spu){
        spu.setSaleable(true);
        spu.setValid(true);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        int count = spuMapper.insert(spu);
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
    }

    public SpuDetail querySpuDetailById(Long id) {
        return spuDetailMapper.selectByPrimaryKey(id);
    }

    public List<Sku> querySkuBySpuId(Long id) {
        //查询sku
        Sku record = new Sku();
        record.setSpuId(id);
        List<Sku> skus = skuMapper.select(record);
//        List<Sku> list = skuMapper.findSkuBySpuId(id);
        for (Sku sku : skus) {
            //同时查询出库存
            sku.setStock(stockMapper.selectByPrimaryKey(sku.getId()).getStock());
        }
        return skus;
    }

    //修改
    @Transactional
    public void update(SpuBo spu) {
        //查询以前的sku
        List<Sku> skus = querySkuBySpuId(spu.getId());
        //如果以前存在，则删除
        if (!CollectionUtils.isEmpty(skus)){
            List<Long> ids = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
            //删除之前的库存
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId",ids);
            stockMapper.deleteByExample(example);

            //删除以前的sku
            Sku record = new Sku();
            record.setSpuId(spu.getId());
            skuMapper.delete(record);
        }

        //新增库sku
        List<Sku> skus1 = spu.getSkus();
        //新增库存
        List<Stock> stocks = new ArrayList<>();
        for (Sku sku : skus1) {
            //保存sku
            sku.setSpuId(spu.getId());
            //初始化时间
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insert(sku);
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stocks.add(stock);
        }

        for (Stock stock : stocks) {
            stockMapper.insert(stock);
        }

        // 更新spu
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        spu.setValid(null);
        spu.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spu);

        // 更新spu详情
        this.spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());

        //发送消息
        sendMessage(spu.getId(),"update");
        rabbitTemplate.convertAndSend("ly.item.exchange2","item.update2",spu.getId());



    }

    public void xiajia(Long id) {
        this.spuMapper.xiajia(id);
    }

    public void shangjia(Long id) {
        this.spuMapper.shangjia(id);
    }

    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }

    public Sku querySkuById(Long skuId) {
        return skuMapper.selectByPrimaryKey(skuId);
    }

    public List<Sku> queryskusByIds(List<Long> longs) {
        ArrayList<Sku> list = new ArrayList<>();
        for (Long aLong : longs) {
            Sku sku = skuMapper.selectByPrimaryKey(aLong);
            list.add(sku);
        }
        return list;
    }

    public Stock queryStockBySkuId(Long id) {
        return stockMapper.selectByPrimaryKey(id);
    }


    public Boolean decreaseStock(List<CartDto> orderDetails) {
        try {
            for (CartDto orderDetail : orderDetails) {
                Integer num = orderDetail.getNum();
                System.out.println("购买的数量:"+num);
                Long skuId = orderDetail.getSkuId();
                Stock stock = stockMapper.selectByPrimaryKey(skuId);
                Integer stock1 = stock.getStock();
                Stock stock2 = new Stock();
                stock2.setStock(stock1 - num);
                stockMapper.updateByPrimaryKeySelective(stock2);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
