package com.xzit.train.business.mapper.cust;

import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface TokenValidMapper {
    int decrease(@Param("trainCode") String trainCode,
                 @Param("date") Date date,
                 @Param("decreaseNum") Integer decreaseNum);
}
