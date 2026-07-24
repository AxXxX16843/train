package com.xzit.train.${module}.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.common.util.SnowUtil;
import com.xzit.train.${module}.domain.${Domain};
import com.xzit.train.${module}.domain.${Domain}Example;
import com.xzit.train.${module}.mapper.${Domain}Mapper;
import com.xzit.train.${module}.req.${Domain}QueryReq;
import com.xzit.train.${module}.req.${Domain}SaveReq;
import com.xzit.train.${module}.resp.${Domain}QueryResp;
import com.xzit.train.${module}.service.${Domain}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ${Domain}ServiceImpl implements ${Domain}Service {

    @Autowired
    private ${Domain}Mapper ${domain}Mapper;

    @Override
    public CommonResp<Object> save(${Domain}SaveReq req) {
        DateTime now = DateTime.now();
        ${Domain} ${domain} = BeanUtil.copyProperties(req, ${Domain}.class);
        ${domain}.setId(SnowUtil.getSnowflakeNextId());
        ${domain}.setCreateTime(now);
        ${domain}.setUpdateTime(now);
        ${domain}Mapper.insert(${domain});
        return new CommonResp<>();
    }

    @Override
    public CommonResp<PageResp<${Domain}QueryResp>> queryList(${Domain}QueryReq req) {
        PageHelper.startPage(req.getPage(), req.getSize());
        List<${Domain}> ${domain}s = ${domain}Mapper.selectByExample(null);
        PageInfo<${Domain}> pageInfo = new PageInfo<>(${domain}s);
        List<${Domain}QueryResp> ${domain}QueryRespList = BeanUtil.copyToList(${domain}s, ${Domain}QueryResp.class);
        PageResp<${Domain}QueryResp> pageResp = new PageResp<>();
        pageResp.setList(${domain}QueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

    @Override
    public CommonResp<Object> modify(${Domain}SaveReq req) {
        DateTime now = DateTime.now();
        ${Domain} ${domain} = new ${Domain}();
        BeanUtil.copyProperties(req, ${domain});
        ${domain}.setUpdateTime(now);
        ${domain}Mapper.updateByPrimaryKeySelective(${domain});
        return new CommonResp<>();
    }

    @Override
    public CommonResp<Object> delete(String ids) {
        List<Long> list = Arrays.stream(ids.split(",")).map(Long::valueOf).toList();
        if (list.isEmpty()) {
            return new CommonResp<>();
        }
        for (Long l : list) {
            ${domain}Mapper.deleteByPrimaryKey(l);
        }
        return new CommonResp<>();
    }
}
