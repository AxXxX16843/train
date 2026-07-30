package com.xzit.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzit.train.business.domain.TrainCarriage;
import com.xzit.train.business.domain.TrainCarriageExample;
import com.xzit.train.business.enums.SeatColEnum;
import com.xzit.train.business.mapper.TrainCarriageMapper;
import com.xzit.train.business.mapper.TrainMapper;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class TrainSeatServiceImpl implements TrainSeatService {

    @Autowired
    private TrainSeatMapper trainSeatMapper;
    @Autowired
    private TrainCarriageMapper trainCarriageMapper;
    @Autowired
    private TrainMapper trainMapper;

    @Override
    @CacheEvict(value = "TrainSeatServiceImpl.queryList", allEntries = true)
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
    @Cacheable(value = "TrainSeatServiceImpl.queryList")
    public CommonResp<PageResp<TrainSeatQueryResp>> queryList(TrainSeatQueryReq req) {

        TrainSeatExample trainSeatExample = new TrainSeatExample();
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
        if(ObjectUtil.isNotNull(req.getTrainCode())&&ObjectUtil.isNotEmpty(req.getTrainCode())){
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<TrainSeat> trainSeats = trainSeatMapper.selectByExample(trainSeatExample);
        PageInfo<TrainSeat> pageInfo = new PageInfo<>(trainSeats);
        List<TrainSeatQueryResp> trainSeatQueryRespList = BeanUtil.copyToList(trainSeats, TrainSeatQueryResp.class);
        PageResp<TrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setList(trainSeatQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

    @Override
    @CacheEvict(value = "TrainSeatServiceImpl.queryList", allEntries = true)
    public CommonResp<Object> modify(TrainSeatSaveReq req) {
        DateTime now = DateTime.now();
        TrainSeat trainSeat = new TrainSeat();
        BeanUtil.copyProperties(req, trainSeat);
        trainSeat.setUpdateTime(now);
        trainSeatMapper.updateByPrimaryKeySelective(trainSeat);
        return new CommonResp<>();
    }

    @Override
    @CacheEvict(value = "TrainSeatServiceImpl.queryList", allEntries = true)
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

    @Override
    @CacheEvict(value = "TrainSeatServiceImpl.queryList", allEntries = true)
    @Transactional
    public CommonResp<Object> genSeat(String trainCode) {
        DateTime now = DateTime.now();
        int seatIndex=1;
        TrainSeat trainSeat = new TrainSeat();
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        trainSeatMapper.deleteByExample(trainSeatExample);
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        TrainCarriageExample.Criteria criteria2 = trainCarriageExample.createCriteria();
        criteria2.andTrainCodeEqualTo(trainCode);
        List<TrainCarriage> trainCarriages = trainCarriageMapper.selectByExample(trainCarriageExample);
        for (TrainCarriage trainCarriage : trainCarriages) {
            List<SeatColEnum> colByType = SeatColEnum.getColByType(trainCarriage.getSeatType());
            for (int i = 1; i <= trainCarriage.getRowCount(); i++) {
                for (SeatColEnum seatColEnum : colByType) {
                    trainSeat.setTrainCode(trainCode);
                    trainSeat.setCarriageIndex(trainCarriage.getIndex());
                    trainSeat.setCol(seatColEnum.getCode());
                    trainSeat.setSeatType(seatColEnum.getType());
                    trainSeat.setRow(StrUtil.fillBefore(String.valueOf(i),'0',2));
                    trainSeat.setUpdateTime(now);
                    trainSeat.setCreateTime(now);
                    trainSeat.setId(SnowUtil.getSnowflakeNextId());
                    trainSeat.setCarriageSeatIndex(seatIndex++);
                    trainSeatMapper.insert(trainSeat);
                }
            }
        }
        return new CommonResp<>();
    }

}
