package com.xzit.train.business.service;

import com.xzit.train.business.req.ConfirmOrderDoReq;
import com.xzit.train.business.req.DailyTrainTicketQueryReq;
import com.xzit.train.common.resp.CommonResp;
import com.xzit.train.common.resp.PageResp;
import com.xzit.train.business.req.ConfirmOrderQueryReq;
import com.xzit.train.business.req.ConfirmOrderSaveReq;
import com.xzit.train.business.resp.ConfirmOrderQueryResp;
import jakarta.validation.Valid;

public interface ConfirmOrderService {

    CommonResp<Object> save(ConfirmOrderSaveReq req);

    CommonResp<PageResp<ConfirmOrderQueryResp>> queryList(ConfirmOrderQueryReq req);

    CommonResp<Object> modify(ConfirmOrderSaveReq req);

    CommonResp<Object> delete(String ids);

    CommonResp<Object> doConfirm(@Valid ConfirmOrderDoReq req);

}
