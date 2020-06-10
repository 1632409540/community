package com.xzy.community.enums;

public enum NotificationStatusEnum {
    UNRead(0),READ(1);

    private Integer Status;

    public Integer getStatus() {
        return Status;
    }

    NotificationStatusEnum(Integer status) {
        Status = status;
    }
}
