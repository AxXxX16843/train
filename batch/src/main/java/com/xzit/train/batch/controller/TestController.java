package com.xzit.train.batch.controller;

import com.xzit.train.common.feign.BusinessFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Slf4j
public class TestController {

    @Autowired
    private BusinessFeignClient businessFeignClient;

    @GetMapping("/hello")
    public String hello() {
        return "hello business";
    }
}
