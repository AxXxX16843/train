package com.xzit.train.member.req;

import com.xzit.train.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TicketQueryReq extends PageReq {


    private Long memberId;

}
