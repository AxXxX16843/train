package com.xzit.train.business.service.impl;

import com.xzit.train.business.domain.DailyTrainSeat;
import com.xzit.train.business.mapper.DailyTrainSeatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ConfirmOrderAfterServiceImpl {

    @Autowired
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Transactional
    public void updateSeat(List<DailyTrainSeat> dailyTrainSeats) {

        for (DailyTrainSeat dailyTrainSeat : dailyTrainSeats) {
            DailyTrainSeat dailyTrainSeat1 = new DailyTrainSeat();
            dailyTrainSeat1.setId(dailyTrainSeat.getId());
            dailyTrainSeat1.setSell(dailyTrainSeat.getSell());
            dailyTrainSeat1.setUpdateTime(new Date());
            dailyTrainSeatMapper.updateByPrimaryKeySelective(dailyTrainSeat1);
        }

    }

}
