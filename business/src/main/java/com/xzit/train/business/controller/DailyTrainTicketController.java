package com.xzit.train.business.controller;

import com.xzit.train.business.req.DailyTrainTicketQueryReq;
import com.xzit.train.business.req.DailyTrainTicketSaveReq;
import com.xzit.train.business.resp.DailyTrainTicketQueryResp;
import com.xzit.train.business.service.DailyTrainTicketService;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/daily-train-ticket")
public class DailyTrainTicketController {

    @Autowired
    private DailyTrainTicketService dailyTrainTicketService;

    @GetMapping("query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq req) {
        return dailyTrainTicketService.queryList(req);
    }
}
