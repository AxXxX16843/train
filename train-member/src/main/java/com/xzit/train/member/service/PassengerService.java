package com.xzit.train.member.service;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.member.req.PassengerQueryReq;
import com.xzit.train.member.req.PassengerSaveReq;
import com.xzit.train.member.resp.PassengerQueryResp;

public interface PassengerService {

    CommonResp<Object> save(PassengerSaveReq req);

    CommonResp<PageResp<PassengerQueryResp>> queryList(PassengerQueryReq req);

    CommonResp<Object> modify(PassengerSaveReq req);

    CommonResp<Object> delete(String ids);
}
