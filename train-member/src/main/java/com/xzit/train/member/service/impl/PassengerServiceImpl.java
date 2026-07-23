package com.xzit.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.util.SnowUtil;
import com.xzit.train.member.domain.Passenger;
import com.xzit.train.member.mapper.PassengerMapper;
import com.xzit.train.member.req.SavePassengerReq;
import com.xzit.train.member.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PassengerServiceImpl implements PassengerService {

    @Autowired
    private PassengerMapper passengerMapper;

    @Override
    public CommonResp<Object> save(SavePassengerReq req) {
        DateTime now = DateTime.now();
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        passenger.setId(SnowUtil.getSnowflakeNextId());
        passenger.setCreateTime(now);
        passenger.setUpdateTime(now);
        passengerMapper.insert(passenger);
        return new CommonResp();
    }
}
