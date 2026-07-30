package com.xzit.train.business.service.impl;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSON;
import com.xzit.train.business.domain.ConfirmOrder;
import com.xzit.train.business.dto.ConfirmOrderDto;
import com.xzit.train.business.enums.ConfirmOrderStatusEnum;
import com.xzit.train.business.enums.RocketMQTopicEnum;
import com.xzit.train.business.mapper.ConfirmOrderMapper;
import com.xzit.train.business.req.ConfirmOrderDoReq;
import com.xzit.train.business.req.ConfirmOrderTicketReq;
import com.xzit.train.business.service.ConfirmOrderBeforeService;
import com.xzit.train.common.context.MemberContext;
import com.xzit.train.common.exception.BusinessException;
import com.xzit.train.common.exception.BusinessExpectionEnum;
import com.xzit.train.common.util.SnowUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class ConfirmOrderBeforeServiceImpl implements ConfirmOrderBeforeService {


    @Autowired
    private ConfirmOrderMapper confirmOrderMapper;

    @Autowired
    private SkTokenServiceImpl skTokenService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    @Override
    public Long beforeOrder(ConfirmOrderDoReq req) {
        Long member = MemberContext.getMember().getId();
        req.setMemberId(member);
        boolean b = skTokenService.validToken(req.getTrainCode(), req.getDate(),member);
        if(!b){
            throw new BusinessException(BusinessExpectionEnum.DO_ERROR);
        }
        DateTime now = DateTime.now();
        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        String endStation = req.getEndStation();
        String startStation = req.getStartStation();

        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setMemberId(req.getMemberId());
        confirmOrder.setDate(date);
        confirmOrder.setTrainCode(trainCode);
        confirmOrder.setStart(startStation);
        confirmOrder.setEnd(endStation);
        confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        List<ConfirmOrderTicketReq> tickets = req.getTickets();
        confirmOrder.setTickets(JSON.toJSONString(tickets));

        confirmOrderMapper.insert(confirmOrder);
        ConfirmOrderDto confirmOrderDto = new ConfirmOrderDto();
        confirmOrderDto.setDate(date);
        confirmOrderDto.setTrainCode(trainCode);
        String jsonString = JSON.toJSONString(confirmOrderDto);
        rocketMQTemplate.convertAndSend(RocketMQTopicEnum.CONFIRM_ORDER.getCode(), jsonString);
        log.info("发送消息：{}", jsonString);

        return confirmOrder.getId();

    }
}
