package com.xzit.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TrainSaveReq {

    private Long id;

    @NotBlank(message = "【车次编号】不能为空")
    private String code;

    @NotBlank(message = "【车次类型】不能为空")
    private String type;

    @NotBlank(message = "【始发站】不能为空")
    private String start;

    @NotBlank(message = "【始发站拼音】不能为空")
    private String startPinyin;

    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "【出发时间】不能为空")
    private Date startTime;

    @NotBlank(message = "【终点站】不能为空")
    private String end;

    @NotBlank(message = "【终点站拼音】不能为空")
    private String endPinyin;

    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "【到站时间】不能为空")
    private Date endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}
