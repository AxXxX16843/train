package com.xzit.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzit.train.business.domain.TrainCarriageExample;
import com.xzit.train.business.domain.TrainSeatExample;
import com.xzit.train.business.mapper.TrainCarriageMapper;
import com.xzit.train.common.exception.BusinessException;
import com.xzit.train.common.exception.BusinessExpectionEnum;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.common.util.SnowUtil;
import com.xzit.train.business.domain.Train;
import com.xzit.train.business.domain.TrainExample;
import com.xzit.train.business.mapper.TrainMapper;
import com.xzit.train.business.req.TrainQueryReq;
import com.xzit.train.business.req.TrainSaveReq;
import com.xzit.train.business.resp.TrainQueryResp;
import com.xzit.train.business.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TrainServiceImpl implements TrainService {

    @Autowired
    private TrainMapper trainMapper;
    @Autowired
    private TrainCarriageMapper trainCarriageMapper;


    @Override
    public CommonResp<Object> save(TrainSaveReq req) {
        TrainExample trainExample = new TrainExample();
        TrainExample.Criteria criteria = trainExample.createCriteria();
        criteria.andCodeEqualTo(req.getCode());
        List<Train> trains = trainMapper.selectByExample(trainExample);
        if(ObjectUtil.isNotEmpty(trains)) {
            throw new BusinessException(BusinessExpectionEnum.TRAIN_IS_EXIST);
        }
        DateTime now = DateTime.now();
        Train train = BeanUtil.copyProperties(req, Train.class);
        train.setId(SnowUtil.getSnowflakeNextId());
        train.setCreateTime(now);
        train.setUpdateTime(now);
        trainMapper.insert(train);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<PageResp<TrainQueryResp>> queryList(TrainQueryReq req) {
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Train> trains = trainMapper.selectByExample(null);
        PageInfo<Train> pageInfo = new PageInfo<>(trains);
        List<TrainQueryResp> trainQueryRespList = BeanUtil.copyToList(trains, TrainQueryResp.class);
        PageResp<TrainQueryResp> pageResp = new PageResp<>();
        pageResp.setList(trainQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

    @Override
    public CommonResp<Object> modify(TrainSaveReq req) {
        DateTime now = DateTime.now();
        Train train = new Train();
        BeanUtil.copyProperties(req, train);
        train.setUpdateTime(now);
        trainMapper.updateByPrimaryKeySelective(train);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<Object> delete(String ids) {
        List<Long> list = Arrays.stream(ids.split(",")).map(Long::valueOf).toList();
        if (list.isEmpty()) {
            return new CommonResp<>();
        }
        for (Long l : list) {
            Train train = trainMapper.selectByPrimaryKey(l);
            TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
            trainCarriageExample.createCriteria().andTrainCodeEqualTo(train.getCode());
            trainCarriageMapper.deleteByExample(trainCarriageExample);
            trainMapper.deleteByPrimaryKey(l);
        }
        return new CommonResp<>();
    }

    @Override
    public CommonResp<List<TrainQueryResp>> queryAll() {
        TrainExample trainExample = new TrainExample();
        trainExample.setOrderByClause("code desc");
        List<Train> trains = trainMapper.selectByExample(trainExample);
        return new CommonResp<>(BeanUtil.copyToList(trains,TrainQueryResp.class));
    }


}
