package com.xzit.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzit.train.business.domain.TrainSeatExample;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TrainStationServiceImpl implements TrainStationService {

    @Autowired
    private TrainStationMapper trainStationMapper;

    @Override
    @CacheEvict(value = "TrainStationServiceImpl.queryList", allEntries = true)
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
    @Cacheable("TrainStationServiceImpl.queryList")
    public CommonResp<PageResp<TrainStationQueryResp>> queryList(TrainStationQueryReq req) {
        TrainStationExample trainStationExample = new TrainStationExample();
        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();
        if(ObjectUtil.isNotNull(req.getTrainCode())&&ObjectUtil.isNotEmpty(req.getTrainCode())){
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<TrainStation> trainStations = trainStationMapper.selectByExample(trainStationExample);
        PageInfo<TrainStation> pageInfo = new PageInfo<>(trainStations);
        List<TrainStationQueryResp> trainStationQueryRespList = BeanUtil.copyToList(trainStations, TrainStationQueryResp.class);
        PageResp<TrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setList(trainStationQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

    @Override
    @CacheEvict(value = "TrainStationServiceImpl.queryList", allEntries = true)
    public CommonResp<Object> modify(TrainStationSaveReq req) {
        DateTime now = DateTime.now();
        TrainStation trainStation = new TrainStation();
        BeanUtil.copyProperties(req, trainStation);
        trainStation.setUpdateTime(now);
        trainStationMapper.updateByPrimaryKeySelective(trainStation);
        return new CommonResp<>();
    }

    @Override
    @CacheEvict(value = "TrainStationServiceImpl.queryList", allEntries = true)
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
