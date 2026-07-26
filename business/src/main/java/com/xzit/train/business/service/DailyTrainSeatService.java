package com.xzit.train.business.service;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.DailyTrainSeatQueryReq;
import com.xzit.train.business.req.DailyTrainSeatSaveReq;
import com.xzit.train.business.resp.DailyTrainSeatQueryResp;

import java.util.Date;

public interface DailyTrainSeatService {

    CommonResp<Object> save(DailyTrainSeatSaveReq req);

    CommonResp<PageResp<DailyTrainSeatQueryResp>> queryList(DailyTrainSeatQueryReq req);

    CommonResp<Object> modify(DailyTrainSeatSaveReq req);

    CommonResp<Object> delete(String ids);

    CommonResp<Object> genDailySeat(String trainCode, Date date);



}
