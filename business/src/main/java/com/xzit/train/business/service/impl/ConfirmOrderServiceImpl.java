package com.xzit.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzit.train.business.domain.*;
import com.xzit.train.business.enums.ConfirmOrderStatusEnum;
import com.xzit.train.business.enums.SeatColEnum;
import com.xzit.train.business.enums.SeatTypeEnum;
import com.xzit.train.business.req.*;
import com.xzit.train.business.service.DailyTrainTicketService;
import com.xzit.train.common.context.MemberContext;
import com.xzit.train.common.exception.BusinessException;
import com.xzit.train.common.exception.BusinessExpectionEnum;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.common.util.SnowUtil;
import com.xzit.train.business.mapper.ConfirmOrderMapper;
import com.xzit.train.business.resp.ConfirmOrderQueryResp;
import com.xzit.train.business.service.ConfirmOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class ConfirmOrderServiceImpl implements ConfirmOrderService {

    @Autowired
    private ConfirmOrderMapper confirmOrderMapper;

    @Autowired
    private DailyTrainTicketService dailyTrainTicketService;

    @Autowired
    private DailyTrainSeatServiceImpl dailyTrainSeatService;

    @Autowired
    private DailyTrainCarriageServiceImpl dailyTrainCarriageService;
    @Autowired
    private ConfirmOrderAfterServiceImpl confirmOrderAfterService;



    @Override
    public CommonResp<Object> save(ConfirmOrderSaveReq req) {
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrderMapper.insert(confirmOrder);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<PageResp<ConfirmOrderQueryResp>> queryList(ConfirmOrderQueryReq req) {
        PageHelper.startPage(req.getPage(), req.getSize());
        List<ConfirmOrder> confirmOrders = confirmOrderMapper.selectByExample(null);
        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrders);
        List<ConfirmOrderQueryResp> confirmOrderQueryRespList = BeanUtil.copyToList(confirmOrders, ConfirmOrderQueryResp.class);
        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setList(confirmOrderQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());
        return new CommonResp<>(pageResp);
    }

    @Override
    public CommonResp<Object> modify(ConfirmOrderSaveReq req) {
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = new ConfirmOrder();
        BeanUtil.copyProperties(req, confirmOrder);
        confirmOrder.setUpdateTime(now);
        confirmOrderMapper.updateByPrimaryKeySelective(confirmOrder);
        return new CommonResp<>();
    }

    @Override
    public CommonResp<Object> delete(String ids) {
        List<Long> list = Arrays.stream(ids.split(",")).map(Long::valueOf).toList();
        if (list.isEmpty()) {
            return new CommonResp<>();
        }
        for (Long l : list) {
            confirmOrderMapper.deleteByPrimaryKey(l);
        }
        return new CommonResp<>();
    }

    @Override
    public CommonResp<Object> doConfirm(ConfirmOrderDoReq req) {
        DateTime now = DateTime.now();
        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        String endStation = req.getEndStation();
        String startStation = req.getStartStation();

        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setMemberId(MemberContext.getMember().getId());
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

        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectTickets(trainCode, startStation, endStation, date);

        List<Integer> abIndexList = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();
        List<String> reqSeatList = new ArrayList<>();
        List<DailyTrainSeat> fineSeatList = new ArrayList<>();
        ConfirmOrderTicketReq ticket0 = tickets.get(0);
        if (StrUtil.isNotBlank(ticket0.getSeat())) {
            log.info("进行了选座");
            List<SeatColEnum> colByType = SeatColEnum.getColByType(ticket0.getSeatType());
            for (int i = 1; i <= 2; i++) {
                for (SeatColEnum seatColEnum : colByType) {
                    reqSeatList.add(i+ seatColEnum.getCode());
                }
            }
            log.info("前端传过来的参考选座数据：{}", reqSeatList);
            for (ConfirmOrderTicketReq ticket : tickets) {
                int indexOf = reqSeatList.indexOf(ticket.getSeat());
                abIndexList.add(indexOf);
            }
            log.info("绝对偏移: {}", abIndexList);
            Integer i = abIndexList.get(0);
            for (Integer integer : abIndexList) {
                int abIndex = integer - i;
                indexList.add(abIndex);
            }
            log.info("得到偏移量:{}", indexList);

            getSeat(fineSeatList,
                    trainCode,
                    date,
                    ticket0.getSeatType(),
                    ticket0.getSeat().split("")[1]
                    , indexList
                    ,dailyTrainTicket.getStartIndex(),dailyTrainTicket.getEndIndex());
        } else {
            for (ConfirmOrderTicketReq ticket : tickets) {
                getSeat(fineSeatList,trainCode, date, ticket.getSeatType(), null, null
                        ,dailyTrainTicket.getStartIndex(),dailyTrainTicket.getEndIndex());
            }
        }
        reduceTicket(tickets, dailyTrainTicket);
        confirmOrderAfterService.updateSeat(dailyTrainTicket,fineSeatList);
        return new CommonResp<>();
    }

    private static void reduceTicket(List<ConfirmOrderTicketReq> tickets, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticket : tickets) {
            String seatType = ticket.getSeatType();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatType);
            switch (seatTypeEnum) {
                case YDZ -> {
                    int i = dailyTrainTicket.getYdz() - 1;
                    if (i < 0) {
                        throw new BusinessException(BusinessExpectionEnum.TICKET_COUNT_ERROR);
                    } else {
                        dailyTrainTicket.setYdz(i);
                    }
                }
                case EDZ -> {
                    int i = dailyTrainTicket.getEdz() - 1;
                    if (i < 0) {
                        throw new BusinessException(BusinessExpectionEnum.TICKET_COUNT_ERROR);
                    } else {
                        dailyTrainTicket.setEdz(i);
                    }
                }
                case RW -> {
                    int i = dailyTrainTicket.getRw() - 1;
                    if (i < 0) {
                        throw new BusinessException(BusinessExpectionEnum.TICKET_COUNT_ERROR);
                    } else {
                        dailyTrainTicket.setRw(i);
                    }
                }
                case YW -> {
                    int i = dailyTrainTicket.getYw() - 1;
                    if (i < 0) {
                        throw new BusinessException(BusinessExpectionEnum.TICKET_COUNT_ERROR);
                    } else {
                        dailyTrainTicket.setYw(i);
                    }
                }
            }
        }
    }

    private void getSeat(List<DailyTrainSeat> finalTrainSeats
            ,String trainCode, Date date, String type,
                         String col, List<Integer> indexList,
                         Integer startIndex,Integer endIndex) {
        List<DailyTrainCarriage> carriages = dailyTrainCarriageService.getCarriage(trainCode, date, type);
        for (DailyTrainCarriage carriage : carriages) {
            Integer index = carriage.getIndex();
            List<DailyTrainSeat> temSeatList=new ArrayList<>();
            List<DailyTrainSeat> seats = dailyTrainSeatService.getSeat(trainCode, date, index);
            for (DailyTrainSeat seat : seats) {
                boolean readyChoose=false;
                for (DailyTrainSeat finalTrainSeat : finalTrainSeats) {
                    if(finalTrainSeat.getId().equals(seat.getId())) {
                        readyChoose=true;
                        break;
                    }
                }
                if(readyChoose) {
                    continue;
                }
                if(ObjectUtil.isNotNull(col)) {
                    if(!col.equals(seat.getCol())) {
                        continue;
                    }
                }

                Integer carriageSeatIndex = seat.getCarriageSeatIndex();
                boolean isChoose = chooseSeat(seat, startIndex, endIndex);
                if (!isChoose) {
                    log.info("未选中座位");
                    continue;
                }else{
                    temSeatList.add(seat);
                }
                boolean isChooseAllSeat= true;
                if (CollUtil.isNotEmpty(indexList)) {
                    for (int i = 1; i <indexList.size() ; i++) {
                        int nextIndex = indexList.get(i) - 1 + carriageSeatIndex;
                        if(nextIndex>seats.size()){
                            isChooseAllSeat = false;
                            break;
                        }
                        DailyTrainSeat dailyTrainSeat = seats.get(nextIndex);
                        boolean nextChoose = chooseSeat(dailyTrainSeat, startIndex, endIndex);
                        if(!nextChoose){
                            isChooseAllSeat = false;
                            break;
                        }else {
                            temSeatList.add(dailyTrainSeat);
                        }
                    }
                }
                if(!isChooseAllSeat){
                    temSeatList.clear();
                    continue;
                }
                finalTrainSeats.addAll(temSeatList);
                return;
            }
        }
    }


    private boolean chooseSeat(DailyTrainSeat seat,Integer startIndex,Integer endIndex) {
        String sell = seat.getSell();
        String buySell = sell.substring(startIndex, endIndex);
        if(Integer.parseInt(buySell)>0) {
            return false;
        }else {
            String curSell = buySell.replace('0', '1');
            curSell= StrUtil.fillBefore(curSell,'0',endIndex);
            curSell= StrUtil.fillAfter(curSell,'0',sell.length());
            log.info("当前售卖情况{}", curSell);
            int newSellInt = NumberUtil.binaryToInt(sell) | NumberUtil.binaryToInt(curSell);
            String newSell = NumberUtil.getBinaryStr(newSellInt);
            newSell= StrUtil.fillBefore(newSell,'0',sell.length());
            seat.setSell(newSell);
            return true;
        }
    }
}






























