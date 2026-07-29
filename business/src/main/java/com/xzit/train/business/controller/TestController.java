package com.xzit.train.business.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.xzit.train.common.feign.MemberFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TestController {

    @Autowired
    private MemberFeignClient memberFeignClient;

    @SentinelResource("hello")
    @GetMapping("/hello")
    public String hello() {
        return memberFeignClient.hello();
    }
}
