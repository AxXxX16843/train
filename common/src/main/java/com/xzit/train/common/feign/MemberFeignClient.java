package com.xzit.train.common.feign;


import com.xzit.train.common.fallback.MemberFallback;
import com.xzit.train.common.req.TicketSaveReq;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "memberService",fallback = MemberFallback.class)
public interface MemberFeignClient {

    @GetMapping("/hello")
    String hello();

    @PostMapping("member/feign/ticket/save")
    CommonResp<Object> save(@Valid @RequestBody TicketSaveReq req);

    @GetMapping("member/feign/ticket/ticket-list")
    CommonResp<PageResp> ticketList(@RequestParam("page") Integer page, @RequestParam("size") Integer size);

}
