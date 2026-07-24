package com.xzit.train.common.exception;

import lombok.Data;

public enum BusinessExpectionEnum {
    MOBILE_IS_EXIST("该用户已存在"),
    MOBILE_IS_NOT_REGISTRY("该手机号未注册"),
    CODE_ERROR("验证码错误"),
    PASSENGER_IS_EXIST("乘客已经存在不可重复添加"),
    STATION_NAME_EXIST("该站点名已存在"),
    LIST_IS_NULL("请先选中要删除的乘客");
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
