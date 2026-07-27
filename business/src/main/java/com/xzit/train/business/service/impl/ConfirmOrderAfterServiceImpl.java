package com.xzit.train.business.service.impl;

import com.xzit.train.business.domain.DailyTrainSeat;
import com.xzit.train.business.domain.DailyTrainTicket;
import com.xzit.train.business.mapper.DailyTrainSeatMapper;
import com.xzit.train.business.mapper.cust.ConfirmOrderCustMapper;
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


    @Transactional
    public void updateSeat(DailyTrainTicket dailyTrainTicket, List<DailyTrainSeat> dailyTrainSeats) {

        for (DailyTrainSeat dailyTrainSeat : dailyTrainSeats) {
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
        }
    }

}
