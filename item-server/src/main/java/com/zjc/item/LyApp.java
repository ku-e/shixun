package com.zjc.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.zjc.item.mapper")
public class LyApp {
    public static void main(String[] args) {
        SpringApplication.run(LyApp.class);
    }
}
