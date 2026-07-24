package com.xzit.train.business.service;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.TrainQueryReq;
import com.xzit.train.business.req.TrainSaveReq;
import com.xzit.train.business.resp.TrainQueryResp;

public interface TrainService {

    CommonResp<Object> save(TrainSaveReq req);

    CommonResp<PageResp<TrainQueryResp>> queryList(TrainQueryReq req);

    CommonResp<Object> modify(TrainSaveReq req);

    CommonResp<Object> delete(String ids);
}
