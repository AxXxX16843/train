package com.xzit.train.member.controller;

import com.xzit.train.member.service.MemberService;
import com.xzit.train.member.service.impl.MemberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class MemberController {
    @Autowired
    private MemberServiceImpl memberService;
    @GetMapping("/count")
    public Integer  count(){
        return memberService.count();
    }
}
