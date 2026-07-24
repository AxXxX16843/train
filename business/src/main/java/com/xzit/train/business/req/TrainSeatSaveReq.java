package com.xzit.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TrainSeatSaveReq {

    private Long id;

    @NotBlank(message = "【车次编号】不能为空")
    private String trainCode;

    @NotNull(message = "【厢序】不能为空")
    private Integer carriageIndex;

    @NotBlank(message = "【排号】不能为空")
    private String row;

    @NotBlank(message = "【列号】不能为空")
    private String col;

    @NotBlank(message = "【座位类型】不能为空")
    private String seatType;

    @NotNull(message = "【同车厢座序】不能为空")
    private Integer carriageSeatIndex;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}
