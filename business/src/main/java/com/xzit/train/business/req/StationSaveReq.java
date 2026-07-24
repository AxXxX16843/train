package com.xzit.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StationSaveReq {

    private Long id;

    @NotBlank(message = "【站名】不能为空")
    private String name;

    @NotBlank(message = "【站名拼音】不能为空")
    private String namePinyin;

    @NotBlank(message = "【站名拼音首字母】不能为空")
    private String namePy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}
