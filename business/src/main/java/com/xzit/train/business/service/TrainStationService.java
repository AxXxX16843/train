package com.xzit.train.business.service;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.TrainStationQueryReq;
import com.xzit.train.business.req.TrainStationSaveReq;
import com.xzit.train.business.resp.TrainStationQueryResp;

public interface TrainStationService {

    CommonResp<Object> save(TrainStationSaveReq req);

    CommonResp<PageResp<TrainStationQueryResp>> queryList(TrainStationQueryReq req);

    CommonResp<Object> modify(TrainStationSaveReq req);

    CommonResp<Object> delete(String ids);
}
