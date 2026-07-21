package com.xzit.train.member.service.impl;

import com.xzit.train.member.mapper.MemberMapper;
import com.xzit.train.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberMapper memberMapper;

    public int count(){
        return Math.toIntExact( memberMapper.countByExample(null));
    }
}
