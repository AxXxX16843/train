package com.xzit.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzit.train.business.domain.*;
import com.xzit.train.business.mapper.TrainStationMapper;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.common.util.SnowUtil;
import com.xzit.train.business.mapper.DailyTrainTicketMapper;
import com.xzit.train.business.req.DailyTrainTicketQueryReq;
import com.xzit.train.business.req.DailyTrainTicketSaveReq;
import com.xzit.train.business.resp.DailyTrainTicketQueryResp;
import com.xzit.train.business.service.DailyTrainTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class DailyTrainTicketServiceImpl implements DailyTrainTicketService {

    @Autowired
    private DailyTrainTicketMapper dailyTrainTicketMapper;

    @Autowired
    private TrainStationMapper trainStationMapper;

    @Override
    public CommonResp<Object> save(DailyTrainTicketSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(req, DailyTrainTicket.class);
        dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
        dailyTrainTicket.setCreateTime(now);
        dailyTrainTicket.setUpdateTime(now);
        dailyTrainTicketMapper.insert(dailyTrainTicket);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(DailyTrainTicketQueryReq req) {
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrainTicket> dailyTrainTickets = dailyTrainTicketMapper.selectByExample(null);
        PageInfo<DailyTrainTicket> pageInfo = new PageInfo<>(dailyTrainTickets);
        List<DailyTrainTicketQueryResp> dailyTrainTicketQueryRespList = BeanUtil.copyToList(dailyTrainTickets, DailyTrainTicketQueryResp.class);
        PageResp<DailyTrainTicketQueryResp> pageResp = new PageResp<>();
        pageResp.setList(dailyTrainTicketQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

    @Override
    public CommonResp<Object> modify(DailyTrainTicketSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrainTicket dailyTrainTicket = new DailyTrainTicket();
        BeanUtil.copyProperties(req, dailyTrainTicket);
        dailyTrainTicket.setUpdateTime(now);
        dailyTrainTicketMapper.updateByPrimaryKeySelective(dailyTrainTicket);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<Object> delete(String ids) {
        List<Long> list = Arrays.stream(ids.split(",")).map(Long::valueOf).toList();
        if (list.isEmpty()) {
            return new CommonResp<>();
        }
        for (Long l : list) {
            dailyTrainTicketMapper.deleteByPrimaryKey(l);
        }
        return new CommonResp<>();
    }
    @Override
    public void genDailyTicket(String trainCode, Date date) {
        DateTime now = DateTime.now();
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        DailyTrainTicketExample.Criteria criteria = dailyTrainTicketExample.createCriteria();
        criteria.andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        dailyTrainTicketMapper.deleteByExample(dailyTrainTicketExample);


        TrainStationExample trainStationExample = new TrainStationExample();
        TrainStationExample.Criteria criteria1 = trainStationExample.createCriteria();
        criteria1.andTrainCodeEqualTo(trainCode);
        List<TrainStation> trainStations = trainStationMapper.selectByExample(trainStationExample);
        for (int i = 0; i < trainStations.size(); i++) {
            TrainStation trainStationStart = trainStations.get(i);
            for (int j = i + 1; j < trainStations.size(); j++) {
                TrainStation trainStationEnd = trainStations.get(j);
                DailyTrainTicket dailyTrainTicket = new DailyTrainTicket();
                dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
                dailyTrainTicket.setDate(date);
                dailyTrainTicket.setTrainCode(trainCode);
                dailyTrainTicket.setStart(trainStationStart.getName());
                dailyTrainTicket.setStartPinyin(trainStationStart.getNamePinyin());
                dailyTrainTicket.setStartTime(trainStationStart.getOutTime());
                dailyTrainTicket.setStartIndex(trainStationStart.getIndex());
                dailyTrainTicket.setEnd(trainStationEnd.getName());
                dailyTrainTicket.setEndPinyin(trainStationEnd.getNamePinyin());
                dailyTrainTicket.setEndTime(trainStationEnd.getInTime());
                dailyTrainTicket.setEndIndex(trainStationEnd.getIndex());
                dailyTrainTicket.setYdz(0);
                dailyTrainTicket.setYdzPrice(BigDecimal.ZERO);
                dailyTrainTicket.setEdz(0);
                dailyTrainTicket.setEdzPrice(BigDecimal.ZERO);
                dailyTrainTicket.setRw(0);
                dailyTrainTicket.setRwPrice(BigDecimal.ZERO);
                dailyTrainTicket.setYw(0);
                dailyTrainTicket.setYwPrice(BigDecimal.ZERO);
                dailyTrainTicket.setCreateTime(now);
                dailyTrainTicket.setUpdateTime(now);
                dailyTrainTicketMapper.insert(dailyTrainTicket);
            }
        }

    }


}


























