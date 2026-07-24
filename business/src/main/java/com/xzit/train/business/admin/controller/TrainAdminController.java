package com.xzit.train.business.controller.admin;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.TrainQueryReq;
import com.xzit.train.business.req.TrainSaveReq;
import com.xzit.train.business.resp.TrainQueryResp;
import com.xzit.train.business.service.TrainService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train")
public class AdminTrainController {

    @Autowired
    private TrainService trainService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody TrainSaveReq req) {
        return trainService.save(req);
    }
    @GetMapping("query-list")
    public CommonResp<PageResp<TrainQueryResp>> queryList(@Valid TrainQueryReq req) {
        return trainService.queryList(req);
    }
    @PostMapping("update")
    public CommonResp<Object> modify(@Valid @RequestBody TrainSaveReq req) {
        return trainService.modify(req);
    }
    @DeleteMapping("delete/{ids}")
    public CommonResp<Object> delete(@PathVariable("ids") String ids) {
        return trainService.delete(ids);
    }

}
