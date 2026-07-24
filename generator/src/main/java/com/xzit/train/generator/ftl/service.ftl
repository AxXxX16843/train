package com.xzit.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzit.train.common.context.MemberContext;
import com.xzit.train.common.exception.BusinessException;
import com.xzit.train.common.exception.BusinessExpectionEnum;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.common.util.SnowUtil;
import com.xzit.train.member.domain.${Domain};
import com.xzit.train.member.domain.${Domain}Example;
import com.xzit.train.member.mapper.${Domain}Mapper;
import com.xzit.train.member.req.QueryListReq;
import com.xzit.train.member.req.Save${Domain}Req;
import com.xzit.train.member.resp.${Domain}QueryResp;
import com.xzit.train.member.service.${Domain}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ${Domain}ServiceImpl implements ${Domain}Service {

    @Autowired
    private ${Domain}Mapper ${domain}Mapper;

    @Override
    public CommonResp<Object> save(Save${Domain}Req req) {
        Long memberId = MemberContext.getMember().getId();
        DateTime now = DateTime.now();
        ${Domain}Example ${domain}Example = new ${Domain}Example();
        ${domain}Example.createCriteria().andIdCardEqualTo(req.getIdCard());
        List<${Domain}> ${domain}s = ${domain}Mapper.selectByExample(${domain}Example);
        if (ObjectUtil.isNotEmpty(${domain}s)) {
            throw new BusinessException(BusinessExpectionEnum.PASSENGER_IS_EXIST);
        }
        ${Domain} ${domain} = BeanUtil.copyProperties(req, ${Domain}.class);
        ${domain}.setMemberId(memberId);
        ${domain}.setId(SnowUtil.getSnowflakeNextId());
        ${domain}.setCreateTime(now);
        ${domain}.setUpdateTime(now);
        ${domain}Mapper.insert(${domain});
        return new CommonResp<>();
    }

    @Override
    public CommonResp<PageResp<${Domain}QueryResp>> queryList(QueryListReq req) {
        Long memberId = req.getId();
        ${Domain}Example ${domain}Example = new ${Domain}Example();
        ${Domain}Example.Criteria criteria = ${domain}Example.createCriteria();
        if (ObjectUtil.isNotNull(memberId)) {
            criteria.andMemberIdEqualTo(memberId);
        }
        PageHelper.startPage(req.getPage(),req.getSize());
        List<${Domain}> ${domain}s = ${domain}Mapper.selectByExample(${domain}Example);
        PageInfo<${Domain}> pageInfo = new PageInfo<>(${domain}s);
        List<${Domain}QueryResp> ${domain}QueryRespList = BeanUtil.copyToList(${domain}s, ${Domain}QueryResp.class);
        PageResp<${Domain}QueryResp> pageResp = new PageResp<>();
        pageResp.setList(${domain}QueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

    @Override
    public CommonResp<Object> modify(Save${Domain}Req req) {
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
        if (ObjectUtil.isEmpty(list)) {
            throw new BusinessException(BusinessExpectionEnum.LIST_IS_NULL);
        }
        for (Long l : list) {
            ${domain}Mapper.deleteByPrimaryKey(l);
        }
        return new CommonResp<>();
    }
}
