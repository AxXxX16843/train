package com.xzit.train.member.controller;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.member.req.MemberLoginReq;
import com.xzit.train.member.req.MemberRequest;
import com.xzit.train.member.req.SendCodeReq;
import com.xzit.train.member.resp.MemberLoginResp;
import com.xzit.train.member.service.MemberService;
import com.xzit.train.member.service.impl.MemberServiceImpl;
import jakarta.validation.Valid;
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
    public CommonResp<Long> register(@Valid MemberRequest req){
        CommonResp<Long> commonResp = new CommonResp<>();
        commonResp.setContent(memberService.register(req));
        return commonResp;
    }
    @PostMapping("send-code")
    public CommonResp<Long> sendCode(@Valid SendCodeReq req){
        return memberService.sendCode(req);
    }
    @PostMapping("login")
    public CommonResp<MemberLoginResp> login(@Valid MemberLoginReq req){
        return memberService.login(req);
    }

}
