package com.xzit.train.business.service;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.TrainSeatQueryReq;
import com.xzit.train.business.req.TrainSeatSaveReq;
import com.xzit.train.business.resp.TrainSeatQueryResp;

public interface TrainSeatService {

    CommonResp<Object> save(TrainSeatSaveReq req);

    CommonResp<PageResp<TrainSeatQueryResp>> queryList(TrainSeatQueryReq req);

    CommonResp<Object> modify(TrainSeatSaveReq req);

    CommonResp<Object> delete(String ids);

    CommonResp<Object> genSeat(String trainCode);
}
