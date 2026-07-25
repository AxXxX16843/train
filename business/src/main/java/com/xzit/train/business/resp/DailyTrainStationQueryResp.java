package com.xzit.train.business.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class DailyTrainStationQueryResp {

    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date date;

    private String trainCode;

    private Integer index;

    private String name;

    private String namePinyin;

    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private Date inTime;

    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private Date outTime;

    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private Date stopTime;

    private BigDecimal km;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}
