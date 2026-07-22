package com.xzit.train.gateway.filter;

import com.xzit.train.gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
@Slf4j
@Order(-1)
@Component
public class MemberLoginFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if(path.contains("/member/login")
        || path.contains("/member/send-code")
        || path.contains("/admin")
        || path.contains("/hello")){
            log.info("无需校验");
            return chain.filter(exchange);
        }
        String token = exchange.getRequest().getHeaders().getFirst("token");
        if(token == null || token.isEmpty()){
            log.error("token为空校验失败");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        boolean validate = JwtUtil.validate(token);
        if(validate){
            log.info("校验通过");
            return chain.filter(exchange);
        }
        log.error("token过期");
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
