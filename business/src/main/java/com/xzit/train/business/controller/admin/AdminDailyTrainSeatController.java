package com.xzit.train.business.controller.admin;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.DailyTrainSeatQueryReq;
import com.xzit.train.business.req.DailyTrainSeatSaveReq;
import com.xzit.train.business.resp.DailyTrainSeatQueryResp;
import com.xzit.train.business.service.DailyTrainSeatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-seat")
public class AdminDailyTrainSeatController {

    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainSeatSaveReq req) {
        return dailyTrainSeatService.save(req);
    }
    @GetMapping("query-list")
    public CommonResp<PageResp<DailyTrainSeatQueryResp>> queryList(@Valid DailyTrainSeatQueryReq req) {
        return dailyTrainSeatService.queryList(req);
    }
    @PostMapping("update")
    public CommonResp<Object> modify(@Valid @RequestBody DailyTrainSeatSaveReq req) {
        return dailyTrainSeatService.modify(req);
    }
    @DeleteMapping("delete/{ids}")
    public CommonResp<Object> delete(@PathVariable("ids") String ids) {
        return dailyTrainSeatService.delete(ids);
    }

}
