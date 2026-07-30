package com.xzit.train.business.controller.admin;

import com.xzit.train.business.domain.DailyTrainTicket;
import com.xzit.train.business.domain.TrainStation;
import com.xzit.train.common.feign.MemberFeignClient;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.DailyTrainTicketQueryReq;
import com.xzit.train.business.req.DailyTrainTicketSaveReq;
import com.xzit.train.business.resp.DailyTrainTicketQueryResp;
import com.xzit.train.business.service.DailyTrainTicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/daily-train-ticket")
public class AdminDailyTrainTicketController {

    @Autowired
    private DailyTrainTicketService dailyTrainTicketService;

    @Autowired
    private MemberFeignClient memberFeignClient;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainTicketSaveReq req) {
        return dailyTrainTicketService.save(req);
    }
    @GetMapping("query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq req) {
        return dailyTrainTicketService.queryList(req);
    }
    @PostMapping("update")
    public CommonResp<Object> modify(@Valid @RequestBody DailyTrainTicketSaveReq req) {
        return dailyTrainTicketService.modify(req);
    }
    @DeleteMapping("delete/{ids}")
    public CommonResp<Object> delete(@PathVariable("ids") String ids) {
        return dailyTrainTicketService.delete(ids);
    }

    @GetMapping("ticket-list")
    public CommonResp<PageResp> ticketList(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size) {
        return memberFeignClient.ticketList(page, size);
    }
    @GetMapping("/query-station")
    public CommonResp<List<TrainStation>> queryStation(DailyTrainTicket req) {
        return dailyTrainTicketService.queryStation(req);
    }

}
