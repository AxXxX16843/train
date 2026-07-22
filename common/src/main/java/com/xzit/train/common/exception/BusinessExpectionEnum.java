package com.xzit.train.common.exception;

import lombok.Data;

public enum BusinessExpectionEnum {
    MOBILE_IS_EXIST("该用户已存在");
    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    BusinessExpectionEnum(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "BusinessExpectionEnum{" +
                "desc='" + desc + '\'' +
                '}';
    }
}
