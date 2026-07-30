package com.xzit.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzit.train.business.domain.*;
import com.xzit.train.business.mapper.TrainSeatMapper;
import com.xzit.train.business.mapper.TrainStationMapper;
import com.xzit.train.business.mapper.cust.TokenValidMapper;
import com.xzit.train.business.service.TrainSeatService;
import com.xzit.train.business.service.TrainStationService;
import com.xzit.train.common.exception.BusinessException;
import com.xzit.train.common.exception.BusinessExpectionEnum;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.common.util.SnowUtil;
import com.xzit.train.business.mapper.SkTokenMapper;
import com.xzit.train.business.req.SkTokenQueryReq;
import com.xzit.train.business.req.SkTokenSaveReq;
import com.xzit.train.business.resp.SkTokenQueryResp;
import com.xzit.train.business.service.SkTokenService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SkTokenServiceImpl implements SkTokenService {

    @Autowired
    private SkTokenMapper skTokenMapper;
    @Autowired
    private TrainStationMapper trainStationMapper;
    @Autowired
    private TrainSeatMapper trainSeatMapper;
    @Autowired
    private TokenValidMapper tokenValidMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public CommonResp<Object> save(SkTokenSaveReq req) {
        DateTime now = DateTime.now();
        SkToken skToken = BeanUtil.copyProperties(req, SkToken.class);
        skToken.setId(SnowUtil.getSnowflakeNextId());
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);
        skTokenMapper.insert(skToken);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<PageResp<SkTokenQueryResp>> queryList(SkTokenQueryReq req) {
        PageHelper.startPage(req.getPage(), req.getSize());
        List<SkToken> skTokens = skTokenMapper.selectByExample(null);
        PageInfo<SkToken> pageInfo = new PageInfo<>(skTokens);
        List<SkTokenQueryResp> skTokenQueryRespList = BeanUtil.copyToList(skTokens, SkTokenQueryResp.class);
        PageResp<SkTokenQueryResp> pageResp = new PageResp<>();
        pageResp.setList(skTokenQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

    @Override
    public CommonResp<Object> modify(SkTokenSaveReq req) {
        DateTime now = DateTime.now();
        SkToken skToken = new SkToken();
        BeanUtil.copyProperties(req, skToken);
        skToken.setUpdateTime(now);
        skTokenMapper.updateByPrimaryKeySelective(skToken);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<Object> delete(String ids) {
        List<Long> list = Arrays.stream(ids.split(",")).map(Long::valueOf).toList();
        if (list.isEmpty()) {
            return new CommonResp<>();
        }
        for (Long l : list) {
            skTokenMapper.deleteByPrimaryKey(l);
        }
        return new CommonResp<>();
    }

    @Override
    public void genDaily(String code, Date date) {
        SkTokenExample skTokenExample = new SkTokenExample();
        SkTokenExample.Criteria criteria = skTokenExample.createCriteria();
        criteria.andDateEqualTo(date).andTrainCodeEqualTo(code);
        skTokenMapper.deleteByExample(skTokenExample);
        SkToken skToken = new SkToken();
        DateTime now = DateTime.now();
        skToken.setId(SnowUtil.getSnowflakeNextId());
        skToken.setDate(date);
        skToken.setTrainCode(code);
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);
        TrainStationExample trainStationExample = new TrainStationExample();
        TrainStationExample.Criteria criteria1 = trainStationExample.createCriteria();
        criteria1.andTrainCodeEqualTo(code);
        long l1 = trainStationMapper.countByExample(trainStationExample);
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        TrainSeatExample.Criteria criteria2 = trainSeatExample.createCriteria();
        criteria2.andTrainCodeEqualTo(code);
        long l = trainSeatMapper.countByExample(trainSeatExample);
        int count= (int) (l*l1);
        skToken.setCount(count);
        skTokenMapper.insert(skToken);
    }


    public boolean validToken(String code,Date date,Long member) {
        Date cleanDate = DateUtil.beginOfDay(date);
        String LockKey="train:lock:token:"+cleanDate+":"+code+":"+member;
        String redisKey="train:token:"+cleanDate+":"+code;
        try {
            boolean b = redissonClient.getLock(LockKey).tryLock(2,5, TimeUnit.SECONDS);
            if (!b){
                return false;
            }
            String token = stringRedisTemplate.opsForValue().get(redisKey);
            if(ObjectUtil.isNotNull(token)){
                Long decrement = stringRedisTemplate.opsForValue().decrement(redisKey, 1);
                log.info("缓存令牌数：{}",decrement);
                if(decrement<0){
                    return false;
                }else {
                    stringRedisTemplate.expire(redisKey, 1, TimeUnit.MINUTES);
                }
                if(decrement%5 == 0){
                    log.info("准备同步DB: code={}, date={}, decreaseNum=5", code, cleanDate);
                    int rows = tokenValidMapper.decrease(code, cleanDate, 5);
                    log.info("数据库同步结果：{}", rows);
                }
                return true;
            }
            SkTokenExample skTokenExample = new SkTokenExample();
            skTokenExample.createCriteria().andDateEqualTo(cleanDate).andTrainCodeEqualTo(code);
            List<SkToken> skTokens = skTokenMapper.selectByExample(skTokenExample);
            if(ObjectUtil.isEmpty(skTokens)){
                return false;
            }
            SkToken skToken = skTokens.get(0);
            if(skToken.getCount()<=0){
                return false;
            }
            stringRedisTemplate.opsForValue().set(redisKey,String.valueOf(skToken.getCount()),1, TimeUnit.MINUTES);
            return true;

        } catch (InterruptedException e) {
            throw new BusinessException(BusinessExpectionEnum.DO_ERROR);
        }
    }
}
