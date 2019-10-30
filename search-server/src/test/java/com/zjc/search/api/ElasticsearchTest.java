package com.zjc.search.api;

import com.zjc.common.pojo.PageResult;
import com.zjc.search.dao.GoodsRepository;
import com.zjc.search.pojo.Goods;
import com.zjc.search.pojo.SearchRequest;
import com.zjc.search.service.IndexService;
import com.zjc.shop.pojo.Spu;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jcajce.provider.asymmetric.GOST;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)

public class ElasticsearchTest {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private IndexService indexService;

    @Test
    public void createIndex(){
        //创建索引
        elasticsearchTemplate.createIndex(Goods.class);
        //配置映射
        elasticsearchTemplate.putMapping(Goods.class);
    }

    @Test
    public void loadData(){
        int page = 1; //当前页数
        int rows = 10; //每次查10数据
        int size = 0; //剩余的数据
        do{
            //查询spu
            PageResult<Spu> result = this.goodsClient.queryByPage(page, rows, null, true).getBody();

            List<Spu> items= result.getItems();
            List<Goods> list = new ArrayList<>();
            for (Spu spu : items) {
                System.out.println(spu);
                Goods goods = indexService.buildGoods(spu);
                list.add(goods);
            }
            //保存到es中
            goodsRepository.saveAll(list);

            size = items.size();

            page++;
        }while (size == 10);
    }

    @Test
    public void TestSearch(){
        SearchRequest request = new SearchRequest();
        String key = "手机";
        Integer page = request.getPage() -1;//page 从0开始
        Integer size = request.getSize();

        //创建查询构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //查询
        //对结果进行过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","key","subTitle"},null));

        //基本查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("all", key));

        //分页
        queryBuilder.withPageable(PageRequest.of(page, size));

        //返回结果
        Page<Goods> result = goodsRepository.search(queryBuilder.build());

        //解析结果
        long total = result.getTotalElements();
        long totalpage = (total + size - 1) / size;
        System.out.println(result.getContent());
    }


}


