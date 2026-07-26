package com.xzit.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DailyTrainTicketSaveReq {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "【日期】不能为空")
    private Date date;

    @NotBlank(message = "【车次编号】不能为空")
    private String trainCode;

    @NotBlank(message = "【出发站】不能为空")
    private String start;

    @NotBlank(message = "【出发站拼音】不能为空")
    private String startPinyin;

    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "【出发时间】不能为空")
    private Date startTime;

    @NotNull(message = "【出发站序】不能为空")
    private Integer startIndex;

    @NotBlank(message = "【到达站】不能为空")
    private String end;

    @NotBlank(message = "【到达站拼音】不能为空")
    private String endPinyin;

    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "【到站时间】不能为空")
    private Date endTime;

    @NotNull(message = "【到站站序】不能为空")
    private Integer endIndex;

    @NotNull(message = "【一等座余票】不能为空")
    private Integer ydz;

    @NotNull(message = "【一等座票价】不能为空")
    private BigDecimal ydzPrice;

    @NotNull(message = "【二等座余票】不能为空")
    private Integer edz;

    @NotNull(message = "【二等座票价】不能为空")
    private BigDecimal edzPrice;

    @NotNull(message = "【软卧余票】不能为空")
    private Integer rw;

    @NotNull(message = "【软卧票价】不能为空")
    private BigDecimal rwPrice;

    @NotNull(message = "【硬卧余票】不能为空")
    private Integer yw;

    @NotNull(message = "【硬卧票价】不能为空")
    private BigDecimal ywPrice;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}
