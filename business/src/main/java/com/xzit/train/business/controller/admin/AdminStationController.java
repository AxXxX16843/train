package com.xzit.train.business.controller.admin;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.StationQueryReq;
import com.xzit.train.business.req.StationSaveReq;
import com.xzit.train.business.resp.StationQueryResp;
import com.xzit.train.business.service.StationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/station")
public class AdminStationController {

    @Autowired
    private StationService stationService;


    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody StationSaveReq req) {
        return stationService.save(req);
    }
    @GetMapping("query-list")
    public CommonResp<PageResp<StationQueryResp>> queryList(@Valid StationQueryReq req) {
        return stationService.queryList(req);
    }
    @PostMapping("update")
    public CommonResp<Object> modify(@Valid @RequestBody StationSaveReq req) {
        return stationService.modify(req);
    }
    @DeleteMapping("delete/{ids}")
    public CommonResp<Object> delete(@PathVariable("ids") String ids) {
        return stationService.delete(ids);
    }

}

