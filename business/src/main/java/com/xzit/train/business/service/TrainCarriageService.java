package com.xzit.train.business.service;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.TrainCarriageQueryReq;
import com.xzit.train.business.req.TrainCarriageSaveReq;
import com.xzit.train.business.resp.TrainCarriageQueryResp;

public interface TrainCarriageService {

    CommonResp<Object> save(TrainCarriageSaveReq req);

    CommonResp<PageResp<TrainCarriageQueryResp>> queryList(TrainCarriageQueryReq req);

    CommonResp<Object> modify(TrainCarriageSaveReq req);

    CommonResp<Object> delete(String ids);
}
