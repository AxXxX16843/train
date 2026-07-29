package com.xzit.train.business.req;

import com.xzit.train.business.domain.DailyTrainTicket;
import com.xzit.train.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class ConfirmOrderQueryReq extends PageReq {



}
