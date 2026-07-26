package com.xzit.train.business.service;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.DailyTrainTicketQueryReq;
import com.xzit.train.business.req.DailyTrainTicketSaveReq;
import com.xzit.train.business.resp.DailyTrainTicketQueryResp;

import java.util.Date;

public interface DailyTrainTicketService {

    CommonResp<Object> save(DailyTrainTicketSaveReq req);

    CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(DailyTrainTicketQueryReq req);

    CommonResp<Object> modify(DailyTrainTicketSaveReq req);

    CommonResp<Object> delete(String ids);

    void genDailyTicket(String trainCode, Date date);
}
