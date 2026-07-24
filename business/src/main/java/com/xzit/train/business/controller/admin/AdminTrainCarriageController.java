package com.xzit.train.business.controller.admin;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.TrainCarriageQueryReq;
import com.xzit.train.business.req.TrainCarriageSaveReq;
import com.xzit.train.business.resp.TrainCarriageQueryResp;
import com.xzit.train.business.service.TrainCarriageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train-carriage")
public class AdminTrainCarriageController {

    @Autowired
    private TrainCarriageService trainCarriageService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody TrainCarriageSaveReq req) {
        return trainCarriageService.save(req);
    }
    @GetMapping("query-list")
    public CommonResp<PageResp<TrainCarriageQueryResp>> queryList(@Valid TrainCarriageQueryReq req) {
        return trainCarriageService.queryList(req);
    }
    @PostMapping("update")
    public CommonResp<Object> modify(@Valid @RequestBody TrainCarriageSaveReq req) {
        return trainCarriageService.modify(req);
    }
    @DeleteMapping("delete/{ids}")
    public CommonResp<Object> delete(@PathVariable("ids") String ids) {
        return trainCarriageService.delete(ids);
    }

}
