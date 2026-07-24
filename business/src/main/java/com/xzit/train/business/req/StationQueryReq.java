package com.xzit.train.business.req;

import com.xzit.train.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StationQueryReq extends PageReq {

    private Long id;

}
