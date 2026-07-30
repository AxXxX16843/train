package com.xzit.train.business.service;

import com.xzit.train.business.domain.DailyTrainTicket;
import com.xzit.train.business.domain.TrainStation;
import com.xzit.train.business.enums.TrainTypeEnum;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.DailyTrainTicketQueryReq;
import com.xzit.train.business.req.DailyTrainTicketSaveReq;
import com.xzit.train.business.resp.DailyTrainTicketQueryResp;
import jakarta.validation.Valid;

import java.util.Date;
import java.util.List;

public interface DailyTrainTicketService {

    CommonResp<Object> save(DailyTrainTicketSaveReq req);

    CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(DailyTrainTicketQueryReq req);

    CommonResp<Object> modify(DailyTrainTicketSaveReq req);

    CommonResp<Object> delete(String ids);

    void genDailyTicket(String trainCode, Date date, TrainTypeEnum type);

    DailyTrainTicket selectTickets (String trainCode, String start, String end, Date date);

    CommonResp<List<TrainStation>> queryStation(DailyTrainTicket dailyTrainTicket);
}
