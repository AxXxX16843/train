package com.xzit.train.business.service;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.SkTokenQueryReq;
import com.xzit.train.business.req.SkTokenSaveReq;
import com.xzit.train.business.resp.SkTokenQueryResp;

import java.util.Date;

public interface SkTokenService {

    CommonResp<Object> save(SkTokenSaveReq req);

    CommonResp<PageResp<SkTokenQueryResp>> queryList(SkTokenQueryReq req);

    CommonResp<Object> modify(SkTokenSaveReq req);

    CommonResp<Object> delete(String ids);

    void genDaily(String code, Date date);
}
