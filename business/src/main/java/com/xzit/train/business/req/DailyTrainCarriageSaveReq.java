package com.xzit.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DailyTrainCarriageSaveReq {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "【日期】不能为空")
    private Date date;

    @NotBlank(message = "【车次编号】不能为空")
    private String trainCode;

    @NotNull(message = "【箱序】不能为空")
    private Integer index;

    @NotBlank(message = "【座位类型】不能为空")
    private String seatType;

//    @NotNull(message = "【座位数】不能为空")
    private Integer seatCount;

    @NotNull(message = "【排数】不能为空")
    private Integer rowCount;

//    @NotNull(message = "【列数】不能为空")
    private Integer colCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}
