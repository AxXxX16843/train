package com.xzit.train.business.service.impl;

import cn.hutool.core.date.DateTime;
import com.xzit.train.business.domain.ConfirmOrder;
import com.xzit.train.business.domain.ConfirmOrderExample;
import com.xzit.train.business.domain.DailyTrainSeat;
import com.xzit.train.business.domain.DailyTrainTicket;
import com.xzit.train.business.enums.ConfirmOrderStatusEnum;
import com.xzit.train.business.mapper.ConfirmOrderMapper;
import com.xzit.train.business.mapper.DailyTrainSeatMapper;
import com.xzit.train.business.mapper.cust.ConfirmOrderCustMapper;
import com.xzit.train.business.req.ConfirmOrderTicketReq;
import com.xzit.train.common.context.MemberContext;
import com.xzit.train.common.feign.MemberFeignClient;
import com.xzit.train.common.req.TicketSaveReq;
import com.xzit.train.common.util.SnowUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ConfirmOrderAfterServiceImpl {

    @Autowired
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Autowired
    private ConfirmOrderCustMapper confirmOrderCustMapper;

    @Autowired
    private MemberFeignClient memberFeignClient;

    @Autowired
    private ConfirmOrderMapper confirmOrderMapper;



    @Transactional
    public void updateSeat(DailyTrainTicket dailyTrainTicket, List<DailyTrainSeat> dailyTrainSeats,
                           List<ConfirmOrderTicketReq> tickets,
                           ConfirmOrder confirmOrder) {

        for (int j = 0; j < dailyTrainSeats.size(); j++) {
            DailyTrainSeat dailyTrainSeat=dailyTrainSeats.get(j);
            DailyTrainSeat seatForUpdate = new DailyTrainSeat();
            seatForUpdate.setId(dailyTrainSeat.getId());
            seatForUpdate.setSell(dailyTrainSeat.getSell());
            seatForUpdate.setUpdateTime(new Date());
            dailyTrainSeatMapper.updateByPrimaryKeySelective(seatForUpdate);
            int maxStartIndex = dailyTrainTicket.getEndIndex()-1;
            int minEndIndex = dailyTrainTicket.getStartIndex()+1;
            String sell = seatForUpdate.getSell();
            char[] charArray = sell.toCharArray();
            int minStartIndex = 0;
            for (int i = dailyTrainTicket.getStartIndex()-1; i >=0 ; i--) {
                char c = charArray[i];
                if(c=='1'){
                    minStartIndex=i+1;
                    break;
                }
            }
            int maxEndIndex = sell.length();
            for (int i = dailyTrainTicket.getEndIndex()+1; i < sell.length(); i++) {
                char c = charArray[i];
                if(c=='1'){
                    maxEndIndex=i;
                    break;
                }
            }
            log.info("updateSeat params: date={}, train={}, minStart={}, maxStart={}, minEnd={}, maxEnd={}",
                    dailyTrainSeat.getDate(), dailyTrainSeat.getTrainCode(),
                    minStartIndex, maxStartIndex, minEndIndex, maxEndIndex);
            confirmOrderCustMapper.updateBySell(dailyTrainSeat.getDate(),
                    dailyTrainSeat.getTrainCode(),dailyTrainSeat.getSeatType(),
                    minStartIndex,maxStartIndex,minEndIndex,maxEndIndex);
            DateTime now = DateTime.now();
            TicketSaveReq ticketSaveReq = new TicketSaveReq();
            ticketSaveReq.setId(SnowUtil.getSnowflakeNextId());
            ticketSaveReq.setMemberId(MemberContext.getMember().getId());
            ticketSaveReq.setPassengerId(tickets.get(j).getPassengerId());
            ticketSaveReq.setPassengerName(tickets.get(j).getPassengerName());
            ticketSaveReq.setDate(dailyTrainTicket.getDate());
            ticketSaveReq.setTrainCode(dailyTrainTicket.getTrainCode());
            ticketSaveReq.setCarriageIndex(dailyTrainSeat.getCarriageIndex());
            ticketSaveReq.setRow(dailyTrainSeat.getRow());
            ticketSaveReq.setCol(dailyTrainSeat.getCol());
            ticketSaveReq.setStart(dailyTrainTicket.getStart());
            ticketSaveReq.setStartTime(dailyTrainTicket.getStartTime());
            ticketSaveReq.setEnd(dailyTrainTicket.getEnd());
            ticketSaveReq.setEndTime(dailyTrainTicket.getEndTime());
            ticketSaveReq.setSeatType(dailyTrainSeat.getSeatType());
            ticketSaveReq.setCreateTime(now);
            ticketSaveReq.setUpdateTime(now);
            memberFeignClient.save(ticketSaveReq);


            ConfirmOrder confirmOrderFinal = new ConfirmOrder();
            confirmOrderFinal.setId(confirmOrder.getId());
            confirmOrderFinal.setStatus(ConfirmOrderStatusEnum.SUCCESS.getCode());
            confirmOrderFinal.setUpdateTime(new Date());
            confirmOrderMapper.updateByPrimaryKeySelective(confirmOrderFinal);
        }
    }

}
