package com.xzit.train.member.controller;

import com.xzit.train.common.context.MemberContext;
import com.xzit.train.common.req.TicketSaveReq;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.member.req.TicketQueryReq;
import com.xzit.train.member.resp.TicketQueryResp;
import com.xzit.train.member.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feign/ticket")
public class AdminTicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody TicketSaveReq req) {
        return ticketService.save(req);
    }
    @GetMapping("query-list")
    public CommonResp<PageResp<TicketQueryResp>> queryList(@Valid TicketQueryReq req) {
        req.setMemberId(MemberContext.getMember().getId());
        return ticketService.queryList(req);
    }
    @GetMapping("ticket-list")
    public CommonResp<PageResp<TicketQueryResp>> ticketList(@Valid TicketQueryReq req) {
        // 不设 memberId，查询全部
        return ticketService.queryList(req);
    }
}
