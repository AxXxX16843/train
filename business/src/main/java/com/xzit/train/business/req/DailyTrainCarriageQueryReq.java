package com.xzit.train.business.req;

import com.xzit.train.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class DailyTrainCarriageQueryReq extends PageReq {

    private String trainCode;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

}
