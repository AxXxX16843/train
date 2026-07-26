package com.xzit.train.common.feign;


import com.xzit.train.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;

@FeignClient(name = "businessService",url = "http://localhost:8002/business/")
public interface BusinessFeignClient {
    @GetMapping("/admin/daily-train/gen-daily/{date}")
    CommonResp<Object> genDaily(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);
}
