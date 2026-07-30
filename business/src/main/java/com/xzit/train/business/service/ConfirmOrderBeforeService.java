package com.xzit.train.business.service;

import com.xzit.train.business.req.ConfirmOrderDoReq;

public interface ConfirmOrderBeforeService {

    void beforeOrder(ConfirmOrderDoReq req);

}
