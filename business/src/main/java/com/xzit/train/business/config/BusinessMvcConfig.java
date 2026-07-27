package com.xzit.train.business.config;

import com.xzit.train.common.interceptor.MemberLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class BusinessMvcConfig implements WebMvcConfigurer {

    @Autowired
    private MemberLoginInterceptor memberLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(memberLoginInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/business/hello");
    }
}
