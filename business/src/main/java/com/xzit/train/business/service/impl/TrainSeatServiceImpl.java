package com.xzit.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.common.util.SnowUtil;
import com.xzit.train.business.domain.TrainSeat;
import com.xzit.train.business.domain.TrainSeatExample;
import com.xzit.train.business.mapper.TrainSeatMapper;
import com.xzit.train.business.req.TrainSeatQueryReq;
import com.xzit.train.business.req.TrainSeatSaveReq;
import com.xzit.train.business.resp.TrainSeatQueryResp;
import com.xzit.train.business.service.TrainSeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TrainSeatServiceImpl implements TrainSeatService {

    @Autowired
    private TrainSeatMapper trainSeatMapper;

    @Override
    public CommonResp<Object> save(TrainSeatSaveReq req) {
        DateTime now = DateTime.now();
        TrainSeat trainSeat = BeanUtil.copyProperties(req, TrainSeat.class);
        trainSeat.setId(SnowUtil.getSnowflakeNextId());
        trainSeat.setCreateTime(now);
        trainSeat.setUpdateTime(now);
        trainSeatMapper.insert(trainSeat);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<PageResp<TrainSeatQueryResp>> queryList(TrainSeatQueryReq req) {
        PageHelper.startPage(req.getPage(), req.getSize());
        List<TrainSeat> trainSeats = trainSeatMapper.selectByExample(null);
        PageInfo<TrainSeat> pageInfo = new PageInfo<>(trainSeats);
        List<TrainSeatQueryResp> trainSeatQueryRespList = BeanUtil.copyToList(trainSeats, TrainSeatQueryResp.class);
        PageResp<TrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setList(trainSeatQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

    @Override
    public CommonResp<Object> modify(TrainSeatSaveReq req) {
        DateTime now = DateTime.now();
        TrainSeat trainSeat = new TrainSeat();
        BeanUtil.copyProperties(req, trainSeat);
        trainSeat.setUpdateTime(now);
        trainSeatMapper.updateByPrimaryKeySelective(trainSeat);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<Object> delete(String ids) {
        List<Long> list = Arrays.stream(ids.split(",")).map(Long::valueOf).toList();
        if (list.isEmpty()) {
            return new CommonResp<>();
        }
        for (Long l : list) {
            trainSeatMapper.deleteByPrimaryKey(l);
        }
        return new CommonResp<>();
    }
}
