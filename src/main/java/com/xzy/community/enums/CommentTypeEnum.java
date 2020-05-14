package com.xzy.community.enums;

public enum CommentTypeEnum {
    QUESTION(1),COMMENT(2);

    private Integer type;

    public static boolean isExit(Integer type) {
        for (CommentTypeEnum value : CommentTypeEnum.values()) {
            if(type==value.getType()){
                return true;
            }
        }
        return false;
    }


    public Integer getType() {
        return type;
    }

    CommentTypeEnum(Integer type) {
        this.type = type;
    }
}
