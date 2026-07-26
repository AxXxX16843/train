package com.xzit.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DailyTrainSeatSaveReq {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "【日期】不能为空")
    private Date date;

    @NotBlank(message = "【车次编号】不能为空")
    private String trainCode;

    @NotNull(message = "【箱序】不能为空")
    private Integer carriageIndex;

    @NotBlank(message = "【排号】不能为空")
    private String row;

    @NotBlank(message = "【列号】不能为空")
    private String col;

    @NotBlank(message = "【座位类型】不能为空")
    private String seatType;

    @NotNull(message = "【同车箱座序】不能为空")
    private Integer carriageSeatIndex;

    @NotBlank(message = "【售卖情况】不能为空")
    private String sell;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}
