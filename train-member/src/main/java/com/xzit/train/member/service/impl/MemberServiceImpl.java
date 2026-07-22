package com.xzit.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.xzit.train.common.exception.BusinessException;
import com.xzit.train.common.exception.BusinessExpectionEnum;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.util.SnowUtil;
import com.xzit.train.member.domain.Member;
import com.xzit.train.member.domain.MemberExample;
import com.xzit.train.member.mapper.MemberMapper;
import com.xzit.train.member.req.MemberLoginReq;
import com.xzit.train.member.req.MemberRequest;
import com.xzit.train.member.req.SendCodeReq;
import com.xzit.train.member.resp.MemberLoginResp;
import com.xzit.train.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.commons.util.IdUtils;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.IdUtil;

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
        Member members = getMember(mobile);
        if(!ObjectUtil.isNull(members)){
            throw new BusinessException(BusinessExpectionEnum.MOBILE_IS_EXIST);
        }
        Member member = new Member();
        member.setMobile(mobile);
        member.setId(SnowUtil.getSnowflakeNextId());
        memberMapper.insert(member);
        return member.getId();
    }

    @Override
    public CommonResp<Long> sendCode(SendCodeReq req) {
        String mobile = req.getMobile();
        Member members = getMember(mobile);
        if(!ObjectUtil.isNull(members)){
            Member member = new Member();
            member.setMobile(mobile);
            member.setId(SnowUtil.getSnowflakeNextId());
            memberMapper.insert(member);
        }
        String string = RandomUtil.randomNumbers(6);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<MemberLoginResp> login(MemberLoginReq req) {
        String mobile = req.getMobile();
        Member members = getMember(mobile);
        if(ObjectUtil.isNull(members)){
            throw new BusinessException(BusinessExpectionEnum.MOBILE_IS_NOT_REGISTRY);
        }
        if(!"666666".equals(req.getCode())){
            throw new BusinessException(BusinessExpectionEnum.CODE_ERROR);
        }
        MemberLoginResp resp = new MemberLoginResp();
        BeanUtil.copyProperties(members, resp);
        return new CommonResp<>(resp);
    }

    private Member getMember(String mobile) {
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(memberExample);
        if(members.isEmpty()){
          return null;
        }else {
            return members.get(0);
        }
    }
}
