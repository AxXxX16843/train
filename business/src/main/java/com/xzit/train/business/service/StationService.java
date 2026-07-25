package com.xzit.train.business.service;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.StationQueryReq;
import com.xzit.train.business.req.StationSaveReq;
import com.xzit.train.business.resp.StationQueryResp;

import java.util.List;

public interface StationService {

    CommonResp<Object> save(StationSaveReq req);

    CommonResp<PageResp<StationQueryResp>> queryList(StationQueryReq req);

    CommonResp<Object> modify(StationSaveReq req);

    CommonResp<Object> delete(String ids);

    CommonResp<List<StationQueryResp>> queryAll();

}
