package com.xzit.train.business.controller.admin;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.DailyTrainQueryReq;
import com.xzit.train.business.req.DailyTrainSaveReq;
import com.xzit.train.business.resp.DailyTrainQueryResp;
import com.xzit.train.business.service.DailyTrainService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/admin/daily-train")
public class AdminDailyTrainController {

    @Autowired
    private DailyTrainService dailyTrainService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainSaveReq req) {
        return dailyTrainService.save(req);
    }
    @GetMapping("query-list")
    public CommonResp<PageResp<DailyTrainQueryResp>> queryList(@Valid DailyTrainQueryReq req) {
        return dailyTrainService.queryList(req);
    }
    @PostMapping("update")
    public CommonResp<Object> modify(@Valid @RequestBody DailyTrainSaveReq req) {
        return dailyTrainService.modify(req);
    }
    @DeleteMapping("delete/{ids}")
    public CommonResp<Object> delete(@PathVariable("ids") String ids) {
        return dailyTrainService.delete(ids);
    }
    @GetMapping("/gen-daily/{date}")
    public CommonResp<Object> genDaily(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return dailyTrainService.genDaily(date);
    }
}
