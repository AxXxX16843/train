package com.xzit.train.common.exception;

import lombok.Data;

public enum BusinessExpectionEnum {
    MOBILE_IS_EXIST("该用户已存在"),
    MOBILE_IS_NOT_REGISTRY("该手机号未注册"),
    CODE_ERROR("验证码错误"),
    PASSENGER_IS_EXIST("乘客已经存在不可重复添加"),
    STATION_NAME_EXIST("该站点名已存在"),
    LIST_IS_NULL("请先选中要删除的乘客"),
    TRAIN_IS_EXIST("该车次已经存在"),
    CARRIAGE_IS_EXIST("该车厢已存在"),
    TRAIN_IS_NOT_EXIST("无车次"),
    TYPE_IS_EMPTY("没有这种类型的车次"),
    TICKET_COUNT_ERROR("余票不足"),
    SERVICE_ERROR("服务器忙请稍后重试")
    ,SERVICE_LOCK_ERROR("系统繁忙，请稍后重试");
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
