package com.xzit.train.member.service;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.member.req.SavePassengerReq;
import jakarta.validation.Valid;

public interface PassengerService {


    CommonResp<Object> save(@Valid SavePassengerReq req);
}
