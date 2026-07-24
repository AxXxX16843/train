package com.xzit.train.${module}.controller;

import com.xzit.train.common.context.MemberContext;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.${module}.req.${Domain}QueryReq;
import com.xzit.train.${module}.req.${Domain}SaveReq;
import com.xzit.train.${module}.resp.${Domain}QueryResp;
import com.xzit.train.${module}.service.${Domain}Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${do_main}")
public class ${Domain}Controller {

    @Autowired
    private ${Domain}Service ${domain}Service;


    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody ${Domain}SaveReq req) {
        return ${domain}Service.save(req);
    }
    @GetMapping("query-list")
    public CommonResp<PageResp<${Domain}QueryResp>> queryList(@Valid ${Domain}QueryReq req) {
        req.setId(MemberContext.getMember().getId());
        return ${domain}Service.queryList(req);
    }
    @PostMapping("update")
    public CommonResp<Object> modify(@Valid @RequestBody ${Domain}SaveReq req) {
        return ${domain}Service.modify(req);
    }
    @DeleteMapping("delete/{ids}")
    public CommonResp<Object> delete(@PathVariable("ids") String ids) {
        return ${domain}Service.delete(ids);
    }

}

