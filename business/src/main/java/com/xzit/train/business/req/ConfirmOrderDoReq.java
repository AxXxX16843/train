package com.xzit.train.business.req;

import com.xzit.train.business.domain.DailyTrainTicket;
import com.xzit.train.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ConfirmOrderDoReq extends PageReq {

    private String trainCode;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private String startStation;

    private String endStation;

    private Long dailyTrainTicketId;

    private List<ConfirmOrderTicketReq> tickets;

}
