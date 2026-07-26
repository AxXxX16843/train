package com.xzit.train.business.service;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.DailyTrainStationQueryReq;
import com.xzit.train.business.req.DailyTrainStationSaveReq;
import com.xzit.train.business.resp.DailyTrainStationQueryResp;

import java.util.Date;

public interface DailyTrainStationService {

    CommonResp<Object> save(DailyTrainStationSaveReq req);

    CommonResp<PageResp<DailyTrainStationQueryResp>> queryList(DailyTrainStationQueryReq req);

    CommonResp<Object> modify(DailyTrainStationSaveReq req);

    CommonResp<Object> delete(String ids);

    CommonResp<Object> genDailyStation(String train, Date date);
}
