package com.xzit.train.business.controller.admin;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.TrainSeatQueryReq;
import com.xzit.train.business.req.TrainSeatSaveReq;
import com.xzit.train.business.resp.TrainSeatQueryResp;
import com.xzit.train.business.service.TrainSeatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train-seat")
public class AdminTrainSeatController {

    @Autowired
    private TrainSeatService trainSeatService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody TrainSeatSaveReq req) {
        return trainSeatService.save(req);
    }
    @GetMapping("query-list")
    public CommonResp<PageResp<TrainSeatQueryResp>> queryList(@Valid TrainSeatQueryReq req) {
        return trainSeatService.queryList(req);
    }
    @PostMapping("update")
    public CommonResp<Object> modify(@Valid @RequestBody TrainSeatSaveReq req) {
        return trainSeatService.modify(req);
    }
    @DeleteMapping("delete/{ids}")
    public CommonResp<Object> delete(@PathVariable("ids") String ids) {
        return trainSeatService.delete(ids);
    }

}
