package com.xzit.train.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.xzit.train.business.enums.RocketMQTopicEnum;
import com.xzit.train.business.req.ConfirmOrderDoReq;
import com.xzit.train.business.service.ConfirmOrderBeforeService;
import com.xzit.train.common.context.MemberContext;
import com.xzit.train.common.exception.BusinessException;
import com.xzit.train.common.exception.BusinessExpectionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
@Slf4j
@Service
public class ConfirmOrderBeforeServiceImpl implements ConfirmOrderBeforeService {
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SkTokenServiceImpl skTokenService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    @Override
    public void beforeOrder(ConfirmOrderDoReq req) {
        Long member = MemberContext.getMember().getId();
        boolean b = skTokenService.validToken(req.getTrainCode(), req.getDate(),member);
        if(!b){
            throw new BusinessException(BusinessExpectionEnum.DO_ERROR);
        }
        String lockKey="train:lock:confirm:"+req.getTrainCode()+":"+req.getDate();
        RLock lock = null;
        lock = redissonClient.getLock(lockKey);
        boolean isLock = false;
        try {
            isLock = lock.tryLock(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (!isLock) {
            throw new BusinessException(BusinessExpectionEnum.SERVICE_LOCK_ERROR);
        }
        String jsonString = JSON.toJSONString(req);
        rocketMQTemplate.convertAndSend(RocketMQTopicEnum.CONFIRM_ORDER.getCode(), jsonString);
        log.info("发送消息：{}", jsonString);


    }
}
