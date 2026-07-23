package com.xzit.train.member.controller;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.member.domain.Passenger;
import com.xzit.train.member.req.SavePassengerReq;
import com.xzit.train.member.service.PassengerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("passenger")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;


    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody SavePassengerReq req) {
        return passengerService.save(req);
    }
}
