package com.xzit.train.business.controller.admin;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.DailyTrainCarriageQueryReq;
import com.xzit.train.business.req.DailyTrainCarriageSaveReq;
import com.xzit.train.business.resp.DailyTrainCarriageQueryResp;
import com.xzit.train.business.service.DailyTrainCarriageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-carriage")
public class AdminDailyTrainCarriageController {

    @Autowired
    private DailyTrainCarriageService dailyTrainCarriageService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainCarriageSaveReq req) {
        return dailyTrainCarriageService.save(req);
    }
    @GetMapping("query-list")
    public CommonResp<PageResp<DailyTrainCarriageQueryResp>> queryList(@Valid DailyTrainCarriageQueryReq req) {
        return dailyTrainCarriageService.queryList(req);
    }
    @PostMapping("update")
    public CommonResp<Object> modify(@Valid @RequestBody DailyTrainCarriageSaveReq req) {
        return dailyTrainCarriageService.modify(req);
    }
    @DeleteMapping("delete/{ids}")
    public CommonResp<Object> delete(@PathVariable("ids") String ids) {
        return dailyTrainCarriageService.delete(ids);
    }

}
