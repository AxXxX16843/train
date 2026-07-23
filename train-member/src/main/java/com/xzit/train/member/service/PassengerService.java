package com.xzit.train.member.service;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.member.req.QueryListReq;
import com.xzit.train.member.req.SavePassengerReq;
import com.xzit.train.member.resp.PassengerQueryResp;
import jakarta.validation.Valid;

import java.util.List;

public interface PassengerService {


    CommonResp<Object> save(@Valid SavePassengerReq req);

    CommonResp<PageResp<PassengerQueryResp>> queryList(QueryListReq req);

    CommonResp<Object> modify(@Valid SavePassengerReq req);

    CommonResp<Object> delete(String ids);
}
