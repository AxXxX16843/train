package com.xzit.train.business.dto;


import lombok.Data;

import java.util.Date;

@Data
public class ConfirmOrderDto {

    private String trainCode;
    private Date date;

}
