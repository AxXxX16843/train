package com.xzit.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.common.util.SnowUtil;
import com.xzit.train.business.domain.TrainStation;
import com.xzit.train.business.domain.TrainStationExample;
import com.xzit.train.business.mapper.TrainStationMapper;
import com.xzit.train.business.req.TrainStationQueryReq;
import com.xzit.train.business.req.TrainStationSaveReq;
import com.xzit.train.business.resp.TrainStationQueryResp;
import com.xzit.train.business.service.TrainStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TrainStationServiceImpl implements TrainStationService {

    @Autowired
    private TrainStationMapper trainStationMapper;

    @Override
    public CommonResp<Object> save(TrainStationSaveReq req) {
        DateTime now = DateTime.now();
        TrainStation trainStation = BeanUtil.copyProperties(req, TrainStation.class);
        trainStation.setId(SnowUtil.getSnowflakeNextId());
        trainStation.setCreateTime(now);
        trainStation.setUpdateTime(now);
        trainStationMapper.insert(trainStation);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<PageResp<TrainStationQueryResp>> queryList(TrainStationQueryReq req) {
        PageHelper.startPage(req.getPage(), req.getSize());
        List<TrainStation> trainStations = trainStationMapper.selectByExample(null);
        PageInfo<TrainStation> pageInfo = new PageInfo<>(trainStations);
        List<TrainStationQueryResp> trainStationQueryRespList = BeanUtil.copyToList(trainStations, TrainStationQueryResp.class);
        PageResp<TrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setList(trainStationQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

    @Override
    public CommonResp<Object> modify(TrainStationSaveReq req) {
        DateTime now = DateTime.now();
        TrainStation trainStation = new TrainStation();
        BeanUtil.copyProperties(req, trainStation);
        trainStation.setUpdateTime(now);
        trainStationMapper.updateByPrimaryKeySelective(trainStation);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<Object> delete(String ids) {
        List<Long> list = Arrays.stream(ids.split(",")).map(Long::valueOf).toList();
        if (list.isEmpty()) {
            return new CommonResp<>();
        }
        for (Long l : list) {
            trainStationMapper.deleteByPrimaryKey(l);
        }
        return new CommonResp<>();
    }
}
