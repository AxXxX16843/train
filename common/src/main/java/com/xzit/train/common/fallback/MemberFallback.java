package com.xzit.train.common.fallback;

import com.xzit.train.common.feign.BusinessFeignClient;
import com.xzit.train.common.feign.MemberFeignClient;
import com.xzit.train.common.req.TicketSaveReq;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.Date;

public class MemberFallback implements MemberFeignClient {

    @Override
    public String hello() {
        return "";
    }

    @Override
    public CommonResp<Object> save(TicketSaveReq req) {
        return null;
    }

    @Override
    public CommonResp<PageResp> ticketList(Integer page, Integer size) {
        return null;
    }
}
