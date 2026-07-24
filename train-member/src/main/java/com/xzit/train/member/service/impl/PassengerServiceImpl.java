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
import com.xzit.train.member.domain.Passenger;
import com.xzit.train.member.domain.PassengerExample;
import com.xzit.train.member.mapper.PassengerMapper;
import com.xzit.train.member.req.PassengerQueryReq;
import com.xzit.train.member.req.PassengerSaveReq;
import com.xzit.train.member.resp.PassengerQueryResp;
import com.xzit.train.member.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class PassengerServiceImpl implements PassengerService {

    @Autowired
    private PassengerMapper passengerMapper;

    @Override
    public CommonResp<Object> save(PassengerSaveReq req) {
        Long memberId = MemberContext.getMember().getId();
        DateTime now = DateTime.now();
        PassengerExample passengerExample = new PassengerExample();
        passengerExample.createCriteria().andIdCardEqualTo(req.getIdCard());
        List<Passenger> passengers = passengerMapper.selectByExample(passengerExample);
        if (ObjectUtil.isNotEmpty(passengers)) {
            throw new BusinessException(BusinessExpectionEnum.PASSENGER_IS_EXIST);
        }
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        passenger.setMemberId(memberId);
        passenger.setId(SnowUtil.getSnowflakeNextId());
        passenger.setCreateTime(now);
        passenger.setUpdateTime(now);
        passengerMapper.insert(passenger);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<PageResp<PassengerQueryResp>> queryList(PassengerQueryReq req) {
        Long memberId = req.getId();
        PassengerExample passengerExample = new PassengerExample();
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        if (ObjectUtil.isNotNull(memberId)) {
            criteria.andMemberIdEqualTo(memberId);
        }
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Passenger> passengers = passengerMapper.selectByExample(passengerExample);
        PageInfo<Passenger> pageInfo = new PageInfo<>(passengers);
        List<PassengerQueryResp> passengerQueryRespList = BeanUtil.copyToList(passengers, PassengerQueryResp.class);
        PageResp<PassengerQueryResp> pageResp = new PageResp<>();
        pageResp.setList(passengerQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

    @Override
    public CommonResp<Object> modify(PassengerSaveReq req) {
        DateTime now = DateTime.now();
        Passenger passenger = new Passenger();
        BeanUtil.copyProperties(req, passenger);
        passenger.setUpdateTime(now);
        passengerMapper.updateByPrimaryKeySelective(passenger);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<Object> delete(String ids) {
        List<Long> list = Arrays.stream(ids.split(",")).map(Long::valueOf).toList();
        if (ObjectUtil.isEmpty(list)) {
            throw new BusinessException(BusinessExpectionEnum.LIST_IS_NULL);
        }
        for (Long l : list) {
            passengerMapper.deleteByPrimaryKey(l);
        }
        return new CommonResp<>();
    }
}

