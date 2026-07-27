package com.xzit.train.business.mapper.cust;

import java.util.Date;

public interface ConfirmOrderCustMapper {
    int updateBySell(Date date, String trainCode, String type, Integer minStartIndex, Integer maxStartIndex, Integer minEndIndex, Integer maxEndIndex);
}
