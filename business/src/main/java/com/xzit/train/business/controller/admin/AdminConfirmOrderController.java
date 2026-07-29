package com.xzit.train.business.controller.admin;

import com.xzit.train.business.req.ConfirmOrderDoReq;
import com.xzit.train.business.req.DailyTrainTicketQueryReq;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.ConfirmOrderQueryReq;
import com.xzit.train.business.req.ConfirmOrderSaveReq;
import com.xzit.train.business.resp.ConfirmOrderQueryResp;
import com.xzit.train.business.service.ConfirmOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/confirm-order")
public class AdminConfirmOrderController {

    @Autowired
    private ConfirmOrderService confirmOrderService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody ConfirmOrderSaveReq req) {
        return confirmOrderService.save(req);
    }
    @GetMapping("query-list")
    public CommonResp<PageResp<ConfirmOrderQueryResp>> queryList(@Valid ConfirmOrderQueryReq req) {
        return confirmOrderService.queryList(req);
    }
    @PostMapping("update")
    public CommonResp<Object> modify(@Valid @RequestBody ConfirmOrderSaveReq req) {
        return confirmOrderService.modify(req);
    }
    @DeleteMapping("delete/{ids}")
    public CommonResp<Object> delete(@PathVariable("ids") String ids) {
        return confirmOrderService.delete(ids);
    }

    @PostMapping("do-confirm")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req) {
        confirmOrderService.doConfirm(req);
        return new CommonResp<>();
    }

}
