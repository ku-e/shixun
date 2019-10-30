package com.zjc.ams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class LySmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LySmsApplication.class);
    }
}
