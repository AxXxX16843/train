package com.xzit.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzit.train.business.domain.TrainCarriage;
import com.xzit.train.business.domain.TrainCarriageExample;
import com.xzit.train.business.enums.SeatColEnum;
import com.xzit.train.business.mapper.TrainCarriageMapper;
import com.xzit.train.common.exception.BusinessException;
import com.xzit.train.common.exception.BusinessExpectionEnum;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.common.util.SnowUtil;
import com.xzit.train.business.domain.DailyTrainCarriage;
import com.xzit.train.business.domain.DailyTrainCarriageExample;
import com.xzit.train.business.mapper.DailyTrainCarriageMapper;
import com.xzit.train.business.req.DailyTrainCarriageQueryReq;
import com.xzit.train.business.req.DailyTrainCarriageSaveReq;
import com.xzit.train.business.resp.DailyTrainCarriageQueryResp;
import com.xzit.train.business.service.DailyTrainCarriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class DailyTrainCarriageServiceImpl implements DailyTrainCarriageService {

    @Autowired
    private DailyTrainCarriageMapper dailyTrainCarriageMapper;

    @Autowired
    private TrainCarriageMapper trainCarriageMapper;

    @Override
    public CommonResp<Object> save(DailyTrainCarriageSaveReq req) {
        DailyTrainCarriageExample trainCarriageExample = new DailyTrainCarriageExample();
        DailyTrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
        criteria.andTrainCodeEqualTo(req.getTrainCode()).andIndexEqualTo(req.getIndex());
        List<DailyTrainCarriage> trainCarriages = dailyTrainCarriageMapper.selectByExample(trainCarriageExample);
        if (ObjectUtil.isNotEmpty(trainCarriages)) {
            throw new BusinessException(BusinessExpectionEnum.CARRIAGE_IS_EXIST);
        }
        List<SeatColEnum> colByType = SeatColEnum.getColByType(req.getSeatType());
        req.setColCount(colByType.size());
        int count=colByType.size()*req.getRowCount();
        req.setSeatCount(count);
        DateTime now = DateTime.now();
        DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(req, DailyTrainCarriage.class);
        dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());
        dailyTrainCarriage.setCreateTime(now);
        dailyTrainCarriage.setUpdateTime(now);
        dailyTrainCarriageMapper.insert(dailyTrainCarriage);
        return new CommonResp<>();
    }
    @Override
    public CommonResp<PageResp<DailyTrainCarriageQueryResp>> queryList(DailyTrainCarriageQueryReq req) {

        DailyTrainCarriageExample dailyTrainCarriageExample = new DailyTrainCarriageExample();
        DailyTrainCarriageExample.Criteria criteria = dailyTrainCarriageExample.createCriteria();
        if(ObjectUtil.isNotEmpty(req.getTrainCode())){
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }
        if(ObjectUtil.isNotNull(req.getStartTime())){
            criteria.andDateEqualTo(req.getStartTime());
        }
        dailyTrainCarriageExample.setOrderByClause("date desc, train_code asc, `index` asc");
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrainCarriage> dailyTrainCarriages = dailyTrainCarriageMapper.selectByExample(dailyTrainCarriageExample);
        PageInfo<DailyTrainCarriage> pageInfo = new PageInfo<>(dailyTrainCarriages);
        List<DailyTrainCarriageQueryResp> dailyTrainCarriageQueryRespList = BeanUtil.copyToList(dailyTrainCarriages, DailyTrainCarriageQueryResp.class);
        PageResp<DailyTrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setList(dailyTrainCarriageQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

    @Override
    public CommonResp<Object> modify(DailyTrainCarriageSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrainCarriage dailyTrainCarriage = new DailyTrainCarriage();
        BeanUtil.copyProperties(req, dailyTrainCarriage);
        dailyTrainCarriage.setUpdateTime(now);
        dailyTrainCarriageMapper.updateByPrimaryKeySelective(dailyTrainCarriage);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<Object> delete(String ids) {
        List<Long> list = Arrays.stream(ids.split(",")).map(Long::valueOf).toList();
        if (list.isEmpty()) {
            return new CommonResp<>();
        }
        for (Long l : list) {
            dailyTrainCarriageMapper.deleteByPrimaryKey(l);
        }
        return new CommonResp<>();
    }

    @Override
    public CommonResp<Object> genDailyCarriage(String trainCode, Date date) {
        DateTime now = DateTime.now();
        DailyTrainCarriageExample example = new DailyTrainCarriageExample();
        example.createCriteria().andTrainCodeEqualTo(trainCode).andDateEqualTo(date);
        dailyTrainCarriageMapper.deleteByExample(example);

        TrainCarriageExample carriageExample = new TrainCarriageExample();
        carriageExample.createCriteria().andTrainCodeEqualTo(trainCode);
        List<TrainCarriage> carriages = trainCarriageMapper.selectByExample(carriageExample);
        for (TrainCarriage carriage : carriages) {
            DailyTrainCarriage dtc = new DailyTrainCarriage();
            BeanUtil.copyProperties(carriage, dtc);
            dtc.setDate(date);
            dtc.setId(SnowUtil.getSnowflakeNextId());
            dtc.setCreateTime(now);
            dtc.setUpdateTime(now);
            dailyTrainCarriageMapper.insert(dtc);
        }
        return new CommonResp<>();
    }
}
