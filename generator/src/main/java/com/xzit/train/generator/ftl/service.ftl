package com.xzit.train.member.service;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.member.req.QueryListReq;
import com.xzit.train.member.req.Save${Domain}Req;
import com.xzit.train.member.resp.${Domain}QueryResp;

public interface ${Domain}Service {

    CommonResp<Object> save(Save${Domain}Req req);

    CommonResp<PageResp<${Domain}QueryResp>> queryList(QueryListReq req);

    CommonResp<Object> modify(Save${Domain}Req req);

    CommonResp<Object> delete(String ids);
}
