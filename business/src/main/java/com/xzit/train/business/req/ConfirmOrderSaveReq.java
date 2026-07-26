package com.xzit.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfirmOrderSaveReq {

    private Long id;

    @NotNull(message = "【会员id】不能为空")
    private Long memberId;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "【日期】不能为空")
    private Date date;

    @NotBlank(message = "【车次编号】不能为空")
    private String trainCode;

    @NotBlank(message = "【出发站】不能为空")
    private String start;

    @NotBlank(message = "【到达站】不能为空")
    private String end;

    @NotNull(message = "【余票ID】不能为空")
    private Long dailyTrainTicketId;

    @NotBlank(message = "【车票】不能为空")
    private String tickets;

    @NotBlank(message = "【订单状态】不能为空")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}
