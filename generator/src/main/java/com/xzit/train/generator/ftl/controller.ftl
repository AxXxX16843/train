package com.xzit.train.member.controller;

import com.xzit.train.common.context.MemberContext;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.member.domain.${Domain};
import com.xzit.train.member.req.QueryListReq;
import com.xzit.train.member.req.Save${Domain}Req;
import com.xzit.train.member.resp.${Domain}QueryResp;
import com.xzit.train.member.service.${Domain}Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${do_main}")
public class ${Domain}Controller {

    @Autowired
    private ${Domain}Service ${domain}Service;


    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody Save${Domain}Req req) {
        return ${domain}Service.save(req);
    }
    @GetMapping("query-list")
    public CommonResp<PageResp<${Domain}QueryResp>> queryList(@Valid QueryListReq req) {
        req.setId(MemberContext.getMember().getId());
        return ${domain}Service.queryList(req);
    }
    @PostMapping("update")
    public CommonResp<Object> modify(@Valid @RequestBody Save${Domain}Req req) {
        return ${domain}Service.modify(req);
    }
    @DeleteMapping("delete/{ids}")
    public CommonResp<Object> delete(@PathVariable("ids") String ids) {
        return ${domain}Service.delete(ids);
    }

}
