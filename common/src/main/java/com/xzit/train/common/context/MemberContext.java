package com.xzit.train.common.context;

import com.xzit.train.common.resp.MemberLoginResp;

public class MemberContext {

    public final static ThreadLocal<MemberLoginResp> member=new ThreadLocal<>();
    public static MemberLoginResp getMember() {
        return member.get();
    }
    public static void setMember(MemberLoginResp member) {
        MemberContext.member.set(member);
    }
}
