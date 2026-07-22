package com.xzit.train.member.service.impl;

import com.xzit.train.common.exception.BusinessException;
import com.xzit.train.common.exception.BusinessExpectionEnum;
import com.xzit.train.member.domain.Member;
import com.xzit.train.member.domain.MemberExample;
import com.xzit.train.member.mapper.MemberMapper;
import com.xzit.train.member.req.MemberRequest;
import com.xzit.train.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberMapper memberMapper;

    public int count(){
        return Math.toIntExact( memberMapper.countByExample(null));
    }

    @Override
    public Long register(MemberRequest request) {
        String mobile = request.getMobile();
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(memberExample);
        if(!members.isEmpty()){
            throw new BusinessException(BusinessExpectionEnum.MOBILE_IS_EXIST);
        }
        Member member = new Member();
        member.setMobile(mobile);
        member.setId(1L);
        memberMapper.insert(member);
        return member.getId();
    }
}
