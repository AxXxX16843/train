package com.xzit.train.business.service;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.DailyTrainCarriageQueryReq;
import com.xzit.train.business.req.DailyTrainCarriageSaveReq;
import com.xzit.train.business.resp.DailyTrainCarriageQueryResp;

public interface DailyTrainCarriageService {

    CommonResp<Object> save(DailyTrainCarriageSaveReq req);

    CommonResp<PageResp<DailyTrainCarriageQueryResp>> queryList(DailyTrainCarriageQueryReq req);

    CommonResp<Object> modify(DailyTrainCarriageSaveReq req);

    CommonResp<Object> delete(String ids);
}
