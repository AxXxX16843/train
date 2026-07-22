package com.xzit.train.member.service;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.member.domain.Member;
import com.xzit.train.member.req.MemberLoginReq;
import com.xzit.train.member.req.MemberRequest;
import com.xzit.train.member.req.SendCodeReq;
import com.xzit.train.member.resp.MemberLoginResp;
import jakarta.validation.Valid;

public interface MemberService {

    int count();
    Long register(MemberRequest request);

    CommonResp<Long> sendCode(@Valid SendCodeReq req);

    CommonResp<MemberLoginResp> login(@Valid MemberLoginReq req);
}
