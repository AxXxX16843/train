package com.xzit.train.common.interceptor;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xzit.train.common.context.MemberContext;
import com.xzit.train.common.resp.MemberLoginResp;
import com.xzit.train.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class MemberLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        String token = request.getHeader("token");
        JSONObject jsonObject = JwtUtil.getJSONObject(token);
        MemberLoginResp resp = JSONUtil.toBean(jsonObject, MemberLoginResp.class);
        log.info("解析出的会员信息：id={}, mobile={}", resp.getId(), resp.getMobile());
        MemberContext.setMember(resp);
        return true;
    }
}
