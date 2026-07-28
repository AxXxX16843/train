package com.xzit.train.member.service;

import com.xzit.train.common.req.TicketSaveReq;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.member.req.TicketQueryReq;
import com.xzit.train.member.resp.TicketQueryResp;

public interface TicketService {

    CommonResp<Object> save(TicketSaveReq req);

    CommonResp<PageResp<TicketQueryResp>> queryList(TicketQueryReq req);

}
