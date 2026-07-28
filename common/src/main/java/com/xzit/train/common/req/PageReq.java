package com.xzit.train.common.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class PageReq implements Serializable {
    @NotNull(message = "页数不能为空")
    private int page;
    @NotNull(message = "页面大小不能为空")
    @Max(value = 100,message = "页面的大小不能超过100")
    private int size;
}
