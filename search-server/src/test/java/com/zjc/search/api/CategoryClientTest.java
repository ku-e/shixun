package com.zjc.search.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CategoryClientTest {
    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void testQueryCategories(){
        ResponseEntity<List<String>> names = categoryClient.queryNameByIds(Arrays.asList(1L, 2L, 3L));
        names.getBody().forEach(System.out::println);
    }
}
