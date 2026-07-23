package com.xzit.train.member.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavePassengerReq {
    private Long id;
    @NotNull(message = "用户id不能为空")
    private Long memberId;
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotBlank(message = "姓名不能为空")
    private String idCard;
    @NotBlank(message = "姓名不能为空")
    private String type;

    private Date createTime;

    private Date updateTime;
}
