package com.xzit.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzit.train.common.context.MemberContext;
import com.xzit.train.common.exception.BusinessException;
import com.xzit.train.common.exception.BusinessExpectionEnum;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.common.util.SnowUtil;
import com.xzit.train.business.domain.Station;
import com.xzit.train.business.domain.StationExample;
import com.xzit.train.business.mapper.StationMapper;
import com.xzit.train.business.req.StationQueryReq;
import com.xzit.train.business.req.StationSaveReq;
import com.xzit.train.business.resp.StationQueryResp;
import com.xzit.train.business.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class StationServiceImpl implements StationService {

    @Autowired
    private StationMapper stationMapper;

    @Override
    public CommonResp<Object> save(StationSaveReq req) {
        StationExample example = new StationExample();
        example.createCriteria().andNameEqualTo(req.getName());
        List<Station> existList = stationMapper.selectByExample(example);
        if (ObjectUtil.isNotEmpty(existList)) {
            throw new BusinessException(BusinessExpectionEnum.STATION_NAME_EXIST);
        }
        DateTime now = DateTime.now();
        Station station = BeanUtil.copyProperties(req, Station.class);
        station.setId(SnowUtil.getSnowflakeNextId());
        station.setCreateTime(now);
        station.setUpdateTime(now);
        stationMapper.insert(station);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<PageResp<StationQueryResp>> queryList(StationQueryReq req) {
//        Long businessId = req.getId();
//        StationExample stationExample = new StationExample();
//        StationExample.Criteria criteria = stationExample.createCriteria();
//        if (ObjectUtil.isNotNull(businessId)) {
//            criteria.andIdEqualTo(businessId);
//        }
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Station> stations = stationMapper.selectByExample(null);
        PageInfo<Station> pageInfo = new PageInfo<>(stations);
        List<StationQueryResp> stationQueryRespList = BeanUtil.copyToList(stations, StationQueryResp.class);
        PageResp<StationQueryResp> pageResp = new PageResp<>();
        pageResp.setList(stationQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

    @Override
    public CommonResp<Object> modify(StationSaveReq req) {
        DateTime now = DateTime.now();
        Station station = new Station();
        BeanUtil.copyProperties(req, station);
        station.setUpdateTime(now);
        stationMapper.updateByPrimaryKeySelective(station);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<Object> delete(String ids) {
        List<Long> list = Arrays.stream(ids.split(",")).map(Long::valueOf).toList();
        if (ObjectUtil.isEmpty(list)) {
            throw new BusinessException(BusinessExpectionEnum.LIST_IS_NULL);
        }
        for (Long l : list) {
            stationMapper.deleteByPrimaryKey(l);
        }
        return new CommonResp<>();
    }
}

