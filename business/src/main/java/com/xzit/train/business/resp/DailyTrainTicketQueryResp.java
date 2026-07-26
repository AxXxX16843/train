package com.xzit.train.business.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class DailyTrainTicketQueryResp {

    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date date;

    private String trainCode;

    private String start;

    private String startPinyin;

    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private Date startTime;

    private Integer startIndex;

    private String end;

    private String endPinyin;

    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private Date endTime;

    private Integer endIndex;

    private Integer ydz;

    private BigDecimal ydzPrice;

    private Integer edz;

    private BigDecimal edzPrice;

    private Integer rw;

    private BigDecimal rwPrice;

    private Integer yw;

    private BigDecimal ywPrice;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}
