package com.xzit.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TrainStationSaveReq {

    private Long id;

    @NotBlank(message = "【车次编号】不能为空")
    private String trainCode;

    @NotNull(message = "【站序】不能为空")
    private Integer index;

    @NotBlank(message = "【站名】不能为空")
    private String name;

    @NotBlank(message = "【站名拼音】不能为空")
    private String namePinyin;

    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private Date inTime;

    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private Date outTime;

    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private Date stopTime;

    @NotNull(message = "【里程（公里）】不能为空")
    private BigDecimal km;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}
