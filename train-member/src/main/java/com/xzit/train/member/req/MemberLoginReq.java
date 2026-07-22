package com.xzit.train.member.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MemberLoginReq {
    @NotBlank(message = "手机号不能为空！")
    @Pattern(regexp = "^1[3-9]\\d{9}$",message = "请输入正确的手机号")
    private String mobile;
    @NotBlank(message = "验证码不能为空！")
    private String code;
}
