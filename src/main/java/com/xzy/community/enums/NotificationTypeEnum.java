package com.xzy.community.enums;

public enum NotificationTypeEnum {
    REPLAY_QUESTION(1,"回复了问题"),REPLAY_COMMENT(2,"回复了评论");

    private Integer type;
    private String name;

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    NotificationTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }
    public static String getName(Integer type) {
        for (NotificationTypeEnum value : NotificationTypeEnum.values()) {
            if(type==value.getType()){
                return value.getName();
            }
        }
        return "";
    }
}
