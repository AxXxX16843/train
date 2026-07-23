package com.xzit.train.member.controller;

import com.xzit.train.common.context.MemberContext;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.member.domain.Passenger;
import com.xzit.train.member.req.QueryListReq;
import com.xzit.train.member.req.SavePassengerReq;
import com.xzit.train.member.resp.PassengerQueryResp;
import com.xzit.train.member.service.PassengerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("passenger")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;


    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody SavePassengerReq req) {
        return passengerService.save(req);
    }
    @GetMapping("query-list")
    public CommonResp<PageResp<PassengerQueryResp>> queryList(@Valid QueryListReq req) {
        req.setId(MemberContext.getMember().getId());
        return passengerService.queryList(req);
    }
    @PostMapping("update")
    public CommonResp<Object> modify(@Valid @RequestBody SavePassengerReq req) {
        return passengerService.modify(req);
    }
    @DeleteMapping("delete/{ids}")
    public CommonResp<Object> delete(@PathVariable("ids") String ids) {
        return passengerService.delete(ids);
    }

}
