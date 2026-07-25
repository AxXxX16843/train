package com.xzit.train.business.controller.admin;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.DailyTrainStationQueryReq;
import com.xzit.train.business.req.DailyTrainStationSaveReq;
import com.xzit.train.business.resp.DailyTrainStationQueryResp;
import com.xzit.train.business.service.DailyTrainStationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-station")
public class AdminDailyTrainStationController {

    @Autowired
    private DailyTrainStationService dailyTrainStationService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainStationSaveReq req) {
        return dailyTrainStationService.save(req);
    }
    @GetMapping("query-list")
    public CommonResp<PageResp<DailyTrainStationQueryResp>> queryList(@Valid DailyTrainStationQueryReq req) {
        return dailyTrainStationService.queryList(req);
    }
    @PostMapping("update")
    public CommonResp<Object> modify(@Valid @RequestBody DailyTrainStationSaveReq req) {
        return dailyTrainStationService.modify(req);
    }
    @DeleteMapping("delete/{ids}")
    public CommonResp<Object> delete(@PathVariable("ids") String ids) {
        return dailyTrainStationService.delete(ids);
    }

}
