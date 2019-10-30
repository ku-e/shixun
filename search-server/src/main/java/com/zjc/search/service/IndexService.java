package com.zjc.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zjc.common.pojo.PageResult;
import com.zjc.common.util.JsonUtils;
import com.zjc.common.util.NumberUtils;
import com.zjc.search.api.BrandClient;
import com.zjc.search.api.CategoryClient;
import com.zjc.search.api.GoodsClient;
import com.zjc.search.api.SpecificationClient;
import com.zjc.search.dao.GoodsRepository;
import com.zjc.search.pojo.Goods;
import com.zjc.search.pojo.SearchRequest;
import com.zjc.shop.pojo.*;
import com.zjc.shop.vo.SkuVo;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IndexService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsRepository goodsRepository;

    /**
     * 构建eleasticsearch商品数据
     * @param spu
     * @return
     */
    public Goods buildGoods(Spu spu){
        //获取 标题+分类名称+品牌名称
        String all = getAll(spu);
        //根据spu的id查询对应sku信息
        List<Sku> skus = goodsClient.querySkuBySpuId(spu.getId()).getBody();
        //获取当前spu中的所有sku价格
        Set<Long> price = getPrice(skus);

        List<SkuVo> skuVos = getSkuJson(skus);

        // 获取规格参数数据
        HashMap<String, Object> specs = getSpecs(spu);

        Goods goods = new Goods();
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setSubTitle(spu.getSubTitle());
        goods.setCreateTime(spu.getCreateTime());

        goods.setAll(all);
        goods.setPrice(price);
        goods.setSkus(JsonUtils.toString(skuVos));
        goods.setSpecs(specs);
        return goods;
    }

    /**
     * 获取规格参数
     * @param spu
     * @return
     */
    private HashMap<String,Object> getSpecs(Spu spu) {
        // 获取规格参数
        List<SpecParam> specParams = specificationClient.querySpecParam(null, spu.getCid3(), true, null);
        // 查询商品详情
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spu.getId()).getBody();
        //通用的规格参数
        Map<Long,String> genericSpec = JsonUtils.toMap(spuDetail.getGenericSpec(),Long.class, String.class);
        //特有规格参数
        Map<Long,List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(),
                new TypeReference<Map<Long, List<String>>>() {});

        //定义spec对应的map
        HashMap<String, Object> map = new HashMap<>();

        for (SpecParam param : specParams){
            String key = param.getName();
            Object value = "";
            //判断通用参数
            if (param.getGeneric()) {
                //根据通用参数ID从genericSpec获取参数名称
                value = genericSpec.get(param.getId());
                //判断通用参数是数字参数
                if(param.getNumeric()){
                    if(value != null){
                        //对数字参数进行处理
                        value = chooseSegment(value.toString(), param);
                    }
                }
            }else {
                //特有参数
                value = specialSpec.get(param.getId());
            }
            //存入map
            map.put(key, value);
        }
        return map;
    }

    /**
     * 数字参数处理
     * 例如：100-200 300-400 500-1000 1000以上
     */
    private String chooseSegment(String value, SpecParam param) {
        //数字字符串转换成double类型
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        //以豆号切割
        String[] segments = param.getSegments().split(",");
        for (String segment : segments){
            //以中划线切割
            String[] segs = segment.split("-");
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + param.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + param.getUnit() + "以下";
                }else{
                    result = segment + param.getUnit();
                }
                break;
            }
        }

        return result;
    }

    private List<SkuVo> getSkuJson(List<Sku> skus) {
        List<SkuVo> skuVos = new ArrayList<>();
        for (Sku sku : skus) {
            SkuVo skuVo = new SkuVo();
            BeanUtils.copyProperties(sku,skuVo);
            skuVo.setImage(StringUtils.substringBefore(sku.getImages(), ","));
            skuVos.add(skuVo);
        }
        return skuVos;
    }

    /**
     * 获取当前spu中的所有sku价格
     * @param skus
     * @return
     */
    private Set<Long> getPrice(List<Sku> skus) {
        return skus.stream().map(Sku::getPrice).collect(Collectors.toSet());
    }

    /**
     * 获取 标题+分类名称+品牌名称
     * @param spu
     * @return
     */
    private String getAll(Spu spu) {
        List<String> categories = categoryClient.queryNameByIds(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3())).getBody();

        Brand brand = brandClient.queryBrandById(spu.getBrandId());


        String all = spu.getTitle()
                +StringUtils.join(categories, ",")+brand.getName();

        return all;

    }

    public PageResult<Goods> search(SearchRequest request) {
        String key = request.getKey();
        if(StringUtils.isBlank(key)){
            // 如果用户没搜索条件，我们可以给默认的，或者返回null
            return null;
        }

        Integer page = request.getPage() - 1;// page 从0开始
        Integer size = request.getSize();

        // 1、创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 2、查询
        // 2.1、对结果进行筛选
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"}, null));
        // 2.2、基本查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("all", key));

        // 2.3、分页
        queryBuilder.withPageable(PageRequest.of(page, size));

        // 3、返回结果
        Page<Goods> result = this.goodsRepository.search(queryBuilder.build());

        // 4、解析结果
        long total = result.getTotalElements();
        long totalPage = (total + size - 1) / size;
        return new PageResult<>(total, totalPage, result.getContent());

    }

    public void delete(Long spuId) {
        goodsRepository.deleteById(spuId);
    }
}
