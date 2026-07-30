package com.xzit.train.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.xzit.train.business.domain.ConfirmOrder;
import com.xzit.train.business.dto.ConfirmOrderDto;
import com.xzit.train.business.req.ConfirmOrderDoReq;
import com.xzit.train.business.service.ConfirmOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RocketMQMessageListener(consumerGroup = "default",topic = "CONFIRM_ORDER")
public class ConfirmOrderConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    private ConfirmOrderService confirmOrderService;


    @Override
    public void onMessage(MessageExt messageExt) {
        byte[] body = messageExt.getBody();
        log.info("接收到消息:{}", new String(body));
        ConfirmOrderDto dto = JSON.parseObject(new String(body), ConfirmOrderDto.class);
        confirmOrderService.doConfirm(dto);
    }
}
