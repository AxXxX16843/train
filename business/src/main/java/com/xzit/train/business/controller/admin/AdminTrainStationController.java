package com.xzit.train.business.controller.admin;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.TrainStationQueryReq;
import com.xzit.train.business.req.TrainStationSaveReq;
import com.xzit.train.business.resp.TrainStationQueryResp;
import com.xzit.train.business.service.TrainStationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train-station")
public class AdminTrainStationController {

    @Autowired
    private TrainStationService trainStationService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody TrainStationSaveReq req) {
        return trainStationService.save(req);
    }
    @GetMapping("query-list")
    public CommonResp<PageResp<TrainStationQueryResp>> queryList(@Valid TrainStationQueryReq req) {
        return trainStationService.queryList(req);
    }
    @PostMapping("update")
    public CommonResp<Object> modify(@Valid @RequestBody TrainStationSaveReq req) {
        return trainStationService.modify(req);
    }
    @DeleteMapping("delete/{ids}")
    public CommonResp<Object> delete(@PathVariable("ids") String ids) {
        return trainStationService.delete(ids);
    }

}
