package com.xzit.train.business.controller.admin;

import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.SkTokenQueryReq;
import com.xzit.train.business.req.SkTokenSaveReq;
import com.xzit.train.business.resp.SkTokenQueryResp;
import com.xzit.train.business.service.SkTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/sk-token")
public class AdminSkTokenController {

    @Autowired
    private SkTokenService skTokenService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody SkTokenSaveReq req) {
        return skTokenService.save(req);
    }
    @GetMapping("query-list")
    public CommonResp<PageResp<SkTokenQueryResp>> queryList(@Valid SkTokenQueryReq req) {
        return skTokenService.queryList(req);
    }
    @PostMapping("update")
    public CommonResp<Object> modify(@Valid @RequestBody SkTokenSaveReq req) {
        return skTokenService.modify(req);
    }
    @DeleteMapping("delete/{ids}")
    public CommonResp<Object> delete(@PathVariable("ids") String ids) {
        return skTokenService.delete(ids);
    }

}
