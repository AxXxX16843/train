package com.xzit.train.${module}.service;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.${module}.req.${Domain}QueryReq;
import com.xzit.train.${module}.req.${Domain}SaveReq;
import com.xzit.train.${module}.resp.${Domain}QueryResp;

public interface ${Domain}Service {

    CommonResp<Object> save(${Domain}SaveReq req);

    CommonResp<PageResp<${Domain}QueryResp>> queryList(${Domain}QueryReq req);

    CommonResp<Object> modify(${Domain}SaveReq req);

    CommonResp<Object> delete(String ids);
}
