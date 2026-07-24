package com.xzit.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.common.util.SnowUtil;
import com.xzit.train.business.domain.TrainCarriage;
import com.xzit.train.business.domain.TrainCarriageExample;
import com.xzit.train.business.mapper.TrainCarriageMapper;
import com.xzit.train.business.req.TrainCarriageQueryReq;
import com.xzit.train.business.req.TrainCarriageSaveReq;
import com.xzit.train.business.resp.TrainCarriageQueryResp;
import com.xzit.train.business.service.TrainCarriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TrainCarriageServiceImpl implements TrainCarriageService {

    @Autowired
    private TrainCarriageMapper trainCarriageMapper;

    @Override
    public CommonResp<Object> save(TrainCarriageSaveReq req) {
        DateTime now = DateTime.now();
        TrainCarriage trainCarriage = BeanUtil.copyProperties(req, TrainCarriage.class);
        trainCarriage.setId(SnowUtil.getSnowflakeNextId());
        trainCarriage.setCreateTime(now);
        trainCarriage.setUpdateTime(now);
        trainCarriageMapper.insert(trainCarriage);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<PageResp<TrainCarriageQueryResp>> queryList(TrainCarriageQueryReq req) {
        PageHelper.startPage(req.getPage(), req.getSize());
        List<TrainCarriage> trainCarriages = trainCarriageMapper.selectByExample(null);
        PageInfo<TrainCarriage> pageInfo = new PageInfo<>(trainCarriages);
        List<TrainCarriageQueryResp> trainCarriageQueryRespList = BeanUtil.copyToList(trainCarriages, TrainCarriageQueryResp.class);
        PageResp<TrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setList(trainCarriageQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

    @Override
    public CommonResp<Object> modify(TrainCarriageSaveReq req) {
        DateTime now = DateTime.now();
        TrainCarriage trainCarriage = new TrainCarriage();
        BeanUtil.copyProperties(req, trainCarriage);
        trainCarriage.setUpdateTime(now);
        trainCarriageMapper.updateByPrimaryKeySelective(trainCarriage);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<Object> delete(String ids) {
        List<Long> list = Arrays.stream(ids.split(",")).map(Long::valueOf).toList();
        if (list.isEmpty()) {
            return new CommonResp<>();
        }
        for (Long l : list) {
            trainCarriageMapper.deleteByPrimaryKey(l);
        }
        return new CommonResp<>();
    }
}
