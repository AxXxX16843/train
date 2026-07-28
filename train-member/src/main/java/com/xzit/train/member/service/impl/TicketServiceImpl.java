package com.xzit.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzit.train.common.req.TicketSaveReq;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.common.util.SnowUtil;
import com.xzit.train.member.domain.Ticket;
import com.xzit.train.member.domain.TicketExample;
import com.xzit.train.member.mapper.TicketMapper;
import com.xzit.train.member.req.TicketQueryReq;
import com.xzit.train.member.resp.TicketQueryResp;
import com.xzit.train.member.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketMapper ticketMapper;

    @Override
    public CommonResp<Object> save(TicketSaveReq req) {
        DateTime now = DateTime.now();
        Ticket ticket = BeanUtil.copyProperties(req, Ticket.class);
        ticket.setId(SnowUtil.getSnowflakeNextId());
        ticket.setCreateTime(now);
        ticket.setUpdateTime(now);
        ticketMapper.insert(ticket);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<PageResp<TicketQueryResp>> queryList(TicketQueryReq req) {

        TicketExample example = new TicketExample();
        TicketExample.Criteria criteria = example.createCriteria();

        if (ObjectUtil.isNotNull(req.getMemberId())) {
            criteria.andMemberIdEqualTo(req.getMemberId());
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Ticket> tickets = ticketMapper.selectByExample(example);
        PageInfo<Ticket> pageInfo = new PageInfo<>(tickets);
        List<TicketQueryResp> ticketQueryRespList = BeanUtil.copyToList(tickets, TicketQueryResp.class);
        PageResp<TicketQueryResp> pageResp = new PageResp<>();
        pageResp.setList(ticketQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

}
