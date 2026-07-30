package com.xzit.train.business.controller.admin;

import com.xzit.train.business.domain.ConfirmOrder;
import com.xzit.train.business.req.ConfirmOrderDoReq;
import com.xzit.train.business.req.DailyTrainTicketQueryReq;
import com.xzit.train.business.service.ConfirmOrderBeforeService;
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
    @Autowired
    private ConfirmOrderBeforeService confirmOrderBeforeService;

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
        Long l = confirmOrderBeforeService.beforeOrder(req);
        return new CommonResp<>(String.valueOf(l));
    }
    @GetMapping("/query-rank/{id}")
    public CommonResp<Integer> queryRank(@PathVariable("id") Long id) {
        return confirmOrderService.queryRank(id);
    }
    @PostMapping("/cancel-order/{id}")
    public CommonResp<Integer> cancelOrder(@PathVariable("id") Long id) {
        return confirmOrderService.cancelOrder(id);
    }

}
