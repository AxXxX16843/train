package com.xzit.train.member.service;

import com.xzit.train.member.domain.Member;
import com.xzit.train.member.req.MemberRequest;

public interface MemberService {

    int count();
    Long register(MemberRequest request);
}
