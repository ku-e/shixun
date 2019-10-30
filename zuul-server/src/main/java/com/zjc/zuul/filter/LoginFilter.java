package com.zjc.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.zjc.common.util.CookieUtils;
import com.zjc.zuul.properties.FilterProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
@EnableConfigurationProperties({FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private FilterProperties filterProps;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String requestURI = request.getRequestURI();
        //判断白名单
        return !isAllowPath(requestURI);
    }

    /**
     * 判断请求URI是不是白名单中的URI，允许直接访问
     * @param requestURI
     * @return
     */
    private boolean isAllowPath(String requestURI) {
        for (String allowPath : filterProps.getAllowPaths()) {
            if (requestURI.startsWith(allowPath)){
                //允许
                return true;
            }
        }
        return false;
    }

    @Override
    public Object run(){
        //获取上下文
        RequestContext context = RequestContext.getCurrentContext();
        //获取请求
        HttpServletRequest request = context.getRequest();
        String token = CookieUtils.getCookieValue(request, "LY_TOKEN");
        String userString = redisTemplate.opsForValue().get(token);
        if (StringUtils.isBlank(userString)){
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(403);
        }
        return null;
    }
}
