package com.xzit.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xzit.train.business.domain.*;
import com.xzit.train.business.mapper.TrainSeatMapper;
import com.xzit.train.business.mapper.TrainStationMapper;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.common.util.SnowUtil;
import com.xzit.train.business.mapper.DailyTrainSeatMapper;
import com.xzit.train.business.req.DailyTrainSeatQueryReq;
import com.xzit.train.business.req.DailyTrainSeatSaveReq;
import com.xzit.train.business.resp.DailyTrainSeatQueryResp;
import com.xzit.train.business.service.DailyTrainSeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class DailyTrainSeatServiceImpl implements DailyTrainSeatService {

    @Autowired
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Autowired
    private TrainStationMapper trainStationMapper;

    @Autowired
    private TrainSeatMapper trainSeatMapper;



    @Override
    public CommonResp<Object> save(DailyTrainSeatSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(req, DailyTrainSeat.class);
        dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
        dailyTrainSeat.setCreateTime(now);
        dailyTrainSeat.setUpdateTime(now);
        dailyTrainSeatMapper.insert(dailyTrainSeat);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<PageResp<DailyTrainSeatQueryResp>> queryList(DailyTrainSeatQueryReq req) {
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        DailyTrainSeatExample.Criteria criteria = dailyTrainSeatExample.createCriteria();
        if(ObjectUtil.isNotNull(req.getTrainCode())&& ObjectUtil.isNotEmpty(req.getTrainCode())){
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }
        if(ObjectUtil.isNotNull(req.getStartTime())){
            criteria.andDateEqualTo(req.getStartTime());
        }
        dailyTrainSeatExample.setOrderByClause("date desc, train_code asc, carriage_index asc, carriage_seat_index asc");
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrainSeat> dailyTrainSeats = dailyTrainSeatMapper.selectByExample(dailyTrainSeatExample);
        PageInfo<DailyTrainSeat> pageInfo = new PageInfo<>(dailyTrainSeats);
        List<DailyTrainSeatQueryResp> dailyTrainSeatQueryRespList = BeanUtil.copyToList(dailyTrainSeats, DailyTrainSeatQueryResp.class);
        PageResp<DailyTrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setList(dailyTrainSeatQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

    @Override
    public CommonResp<Object> modify(DailyTrainSeatSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrainSeat dailyTrainSeat = new DailyTrainSeat();
        BeanUtil.copyProperties(req, dailyTrainSeat);
        dailyTrainSeat.setUpdateTime(now);
        dailyTrainSeatMapper.updateByPrimaryKeySelective(dailyTrainSeat);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<Object> delete(String ids) {
        List<Long> list = Arrays.stream(ids.split(",")).map(Long::valueOf).toList();
        if (list.isEmpty()) {
            return new CommonResp<>();
        }
        for (Long l : list) {
            dailyTrainSeatMapper.deleteByPrimaryKey(l);
        }
        return new CommonResp<>();
    }

    @Transactional
    @Override
    public CommonResp<Object> genDailySeat(String trainCode, Date date) {
        DateTime now = DateTime.now();
        DailyTrainSeatExample example = new DailyTrainSeatExample();
        example.createCriteria().andTrainCodeEqualTo(trainCode).andDateEqualTo(date);
        dailyTrainSeatMapper.deleteByExample(example);

        TrainStationExample trainStationExample = new TrainStationExample();
        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        List<TrainStation> trainStations = trainStationMapper.selectByExample(trainStationExample);
        int size = trainStations.size();
        String sell= StrUtil.fillAfter("",'0',size-1);

        TrainSeatExample seatExample = new TrainSeatExample();
        seatExample.createCriteria().andTrainCodeEqualTo(trainCode);
        List<TrainSeat> seats = trainSeatMapper.selectByExample(seatExample);
        for (TrainSeat seat : seats) {
            DailyTrainSeat dts = new DailyTrainSeat();
            BeanUtil.copyProperties(seat, dts);
            dts.setDate(date);
            dts.setId(SnowUtil.getSnowflakeNextId());
            dts.setCreateTime(now);
            dts.setUpdateTime(now);
            dts.setSell(sell);
            dailyTrainSeatMapper.insert(dts);
        }
        return new CommonResp<>();
    }

}
