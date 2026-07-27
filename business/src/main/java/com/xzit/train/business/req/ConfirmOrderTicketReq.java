package com.xzit.train.business.req;

import com.xzit.train.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ConfirmOrderTicketReq extends PageReq {

    private String trainCode;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private String seatType;

    private String passengerCard;

    private String passengerType;

    private String passengerId;

    private String seat;

}
