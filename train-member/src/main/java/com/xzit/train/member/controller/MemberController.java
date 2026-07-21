package com.xzit.train.member.controller;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.member.req.MemberRequest;
import com.xzit.train.member.service.MemberService;
import com.xzit.train.member.service.impl.MemberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class MemberController {
    @Autowired
    private MemberServiceImpl memberService;
    @GetMapping("/count")
    public CommonResp<Integer> count(){
        CommonResp<Integer> integerCommonResp = new CommonResp<>();
        integerCommonResp.setContent(memberService.count());
        return integerCommonResp;
    }
    @PostMapping("register")
    public CommonResp<Long> register(MemberRequest req){
        CommonResp<Long> commonResp = new CommonResp<>();
        commonResp.setContent(memberService.register(req));
        return commonResp;
    }
}
