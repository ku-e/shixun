package com.zjc.order.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    LoginInterceptor loginInterceptor;

    @Override
    public void  addInterceptors(InterceptorRegistry registry){
        //配置登录拦截器
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
    }
}
