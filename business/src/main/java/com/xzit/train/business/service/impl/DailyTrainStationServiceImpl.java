package com.xzit.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzit.train.business.domain.*;
import com.xzit.train.business.mapper.TrainMapper;
import com.xzit.train.business.mapper.TrainStationMapper;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.common.util.SnowUtil;
import com.xzit.train.business.mapper.DailyTrainStationMapper;
import com.xzit.train.business.req.DailyTrainStationQueryReq;
import com.xzit.train.business.req.DailyTrainStationSaveReq;
import com.xzit.train.business.resp.DailyTrainStationQueryResp;
import com.xzit.train.business.service.DailyTrainStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class DailyTrainStationServiceImpl implements DailyTrainStationService {

    @Autowired
    private DailyTrainStationMapper dailyTrainStationMapper;
    @Autowired
    private TrainStationMapper trainStationMapper;

    @Override
    public CommonResp<Object> save(DailyTrainStationSaveReq req) {
        TrainStationExample trainStationExample = new TrainStationExample();
        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();
        criteria.andTrainCodeEqualTo(req.getTrainCode()).andIndexEqualTo(req.getIndex());
        List<TrainStation> trainStations = trainStationMapper.selectByExample(trainStationExample);
        BeanUtil.copyProperties(trainStations,req);
        DateTime now = DateTime.now();
        DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(req, DailyTrainStation.class);
        dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
        dailyTrainStation.setCreateTime(now);
        dailyTrainStation.setUpdateTime(now);
        dailyTrainStationMapper.insert(dailyTrainStation);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<PageResp<DailyTrainStationQueryResp>> queryList(DailyTrainStationQueryReq req) {

        DailyTrainStationExample example = new DailyTrainStationExample();
        example.setOrderByClause("date desc");
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrainStation> dailyTrainStations = dailyTrainStationMapper.selectByExample(example);
        PageInfo<DailyTrainStation> pageInfo = new PageInfo<>(dailyTrainStations);
        List<DailyTrainStationQueryResp> dailyTrainStationQueryRespList = BeanUtil.copyToList(dailyTrainStations, DailyTrainStationQueryResp.class);
        PageResp<DailyTrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setList(dailyTrainStationQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

    @Override
    public CommonResp<Object> modify(DailyTrainStationSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrainStation dailyTrainStation = new DailyTrainStation();
        BeanUtil.copyProperties(req, dailyTrainStation);
        dailyTrainStation.setUpdateTime(now);
        dailyTrainStationMapper.updateByPrimaryKeySelective(dailyTrainStation);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<Object> delete(String ids) {
        List<Long> list = Arrays.stream(ids.split(",")).map(Long::valueOf).toList();
        if (list.isEmpty()) {
            return new CommonResp<>();
        }
        for (Long l : list) {
            dailyTrainStationMapper.deleteByPrimaryKey(l);
        }
        return new CommonResp<>();
    }

    @Transactional
    @Override
    public CommonResp<Object> genDailyStation(String train, Date date) {
        DateTime now = DateTime.now();
        DailyTrainStationExample dailyTrainStationExample = new DailyTrainStationExample();
        DailyTrainStationExample.Criteria criteria = dailyTrainStationExample.createCriteria();
        criteria.andTrainCodeEqualTo(train).andDateEqualTo(date);
        dailyTrainStationMapper.deleteByExample(dailyTrainStationExample);

        TrainStationExample trainStationExample = new TrainStationExample();
        TrainStationExample.Criteria criteria1 = trainStationExample.createCriteria();
        criteria1.andTrainCodeEqualTo(train);
        List<TrainStation> trainStations = trainStationMapper.selectByExample(trainStationExample);
        for (TrainStation trainStation : trainStations) {
            DailyTrainStation dailyTrainStation = new DailyTrainStation();
            BeanUtil.copyProperties(trainStation,dailyTrainStation);
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStation.setDate(date);
            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStationMapper.insert(dailyTrainStation);
        }
        return new CommonResp<>();
    }

}
