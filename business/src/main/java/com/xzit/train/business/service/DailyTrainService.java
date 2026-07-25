package com.xzit.train.business.service;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.DailyTrainQueryReq;
import com.xzit.train.business.req.DailyTrainSaveReq;
import com.xzit.train.business.resp.DailyTrainQueryResp;

public interface DailyTrainService {

    CommonResp<Object> save(DailyTrainSaveReq req);

    CommonResp<PageResp<DailyTrainQueryResp>> queryList(DailyTrainQueryReq req);

    CommonResp<Object> modify(DailyTrainSaveReq req);

    CommonResp<Object> delete(String ids);
}
