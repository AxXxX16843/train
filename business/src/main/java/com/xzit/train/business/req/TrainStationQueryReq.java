package com.xzit.train.business.req;

import com.xzit.train.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class TrainStationQueryReq extends PageReq implements Serializable {
    private String trainCode;
}
