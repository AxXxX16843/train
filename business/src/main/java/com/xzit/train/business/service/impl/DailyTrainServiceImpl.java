package com.xzit.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzit.train.business.domain.Train;
import com.xzit.train.business.domain.TrainExample;
import com.xzit.train.business.mapper.DailyTrainStationMapper;
import com.xzit.train.business.mapper.TrainMapper;
import com.xzit.train.business.service.*;
import com.xzit.train.common.exception.BusinessException;
import com.xzit.train.common.exception.BusinessExpectionEnum;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.common.util.SnowUtil;
import com.xzit.train.business.domain.DailyTrain;
import com.xzit.train.business.domain.DailyTrainExample;
import com.xzit.train.business.mapper.DailyTrainMapper;
import com.xzit.train.business.req.DailyTrainQueryReq;
import com.xzit.train.business.req.DailyTrainSaveReq;
import com.xzit.train.business.resp.DailyTrainQueryResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class DailyTrainServiceImpl implements DailyTrainService {

    @Autowired
    private DailyTrainMapper dailyTrainMapper;
    @Autowired
    private TrainMapper trainMapper;

    @Autowired
    private DailyTrainStationService dailyTrainStationService;

    @Autowired
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;

    @Autowired
    private DailyTrainTicketService dailyTrainTicketService;


    @Override
    public CommonResp<Object> save(DailyTrainSaveReq req) {
        TrainExample example = new TrainExample();
        TrainExample.Criteria criteria = example.createCriteria();
        criteria.andCodeEqualTo(req.getCode());
        List<Train> trains = trainMapper.selectByExample(example);
        Train train = trains.get(0);
        BeanUtil.copyProperties(train, req);
        DateTime now = DateTime.now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(req, DailyTrain.class);
        dailyTrain.setId(SnowUtil.getSnowflakeNextId());
        dailyTrain.setCreateTime(now);
        dailyTrain.setUpdateTime(now);
        dailyTrainMapper.insert(dailyTrain);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<PageResp<DailyTrainQueryResp>> queryList(DailyTrainQueryReq req) {

        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        dailyTrainExample.setOrderByClause("date desc");
        DailyTrainExample.Criteria criteria = dailyTrainExample.createCriteria();
        if(ObjectUtil.isNotEmpty(req.getTrainCode())){
            criteria.andCodeEqualTo(req.getTrainCode());
        }
        if(ObjectUtil.isNotNull(req.getStartTime())){
            criteria.andDateEqualTo(req.getStartTime());
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrain> dailyTrains = dailyTrainMapper.selectByExample(dailyTrainExample);
        PageInfo<DailyTrain> pageInfo = new PageInfo<>(dailyTrains);
        List<DailyTrainQueryResp> dailyTrainQueryRespList = BeanUtil.copyToList(dailyTrains, DailyTrainQueryResp.class);
        PageResp<DailyTrainQueryResp> pageResp = new PageResp<>();
        pageResp.setList(dailyTrainQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

    @Override
    public CommonResp<Object> modify(DailyTrainSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrain dailyTrain = new DailyTrain();
        BeanUtil.copyProperties(req, dailyTrain);
        dailyTrain.setUpdateTime(now);
        dailyTrainMapper.updateByPrimaryKeySelective(dailyTrain);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<Object> delete(String ids) {
        List<Long> list = Arrays.stream(ids.split(",")).map(Long::valueOf).toList();
        if (list.isEmpty()) {
            return new CommonResp<>();
        }
        for (Long l : list) {
            dailyTrainMapper.deleteByPrimaryKey(l);
        }
        return new CommonResp<>();
    }


    @Transactional
    @Override
    public CommonResp<Object> genDaily(Date date) {
        List<Train> trains = trainMapper.selectByExample(null);
        if(ObjectUtil.isEmpty(trains)){
            throw new BusinessException(BusinessExpectionEnum.TRAIN_IS_NOT_EXIST);
        }
        for (Train train : trains) {
            geneDaily(date, train);
        }
        return new CommonResp<>();
    }

    private void geneDaily(Date date, Train train) {
        DateTime now = DateTime.now();
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        DailyTrainExample.Criteria criteria = dailyTrainExample.createCriteria();
        criteria.andDateEqualTo(date).andCodeEqualTo(train.getCode());
        dailyTrainMapper.deleteByExample(dailyTrainExample);
        DailyTrain dailyTrain = new DailyTrain();
        BeanUtil.copyProperties(train, dailyTrain);
        dailyTrain.setDate(date);
        dailyTrain.setUpdateTime(now);
        dailyTrain.setCreateTime(date);
        dailyTrain.setId(SnowUtil.getSnowflakeNextId());
        dailyTrainMapper.insert(dailyTrain);
        log.info("生成该车次经过站点");
        dailyTrainStationService.genDailyStation(train.getCode(), date);
        log.info("生成该车次车厢");
        dailyTrainCarriageService.genDailyCarriage(train.getCode(), date);
        log.info("生成该车次座位");
        dailyTrainSeatService.genDailySeat(train.getCode(), date);
        log.info("生成每日车票信息");
        dailyTrainTicketService.genDailyTicket(train.getCode(), date);

    }
}




