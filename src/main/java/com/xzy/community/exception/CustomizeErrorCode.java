package com.xzy.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode{

    QUESTION_NOT_FOUND("你找的问题不存在，要不要换一个试试？");
    private String message;
    @Override
    public String getMessage(){
        return message;
    }
    CustomizeErrorCode(String message){
        this.message=message;
    }

}
