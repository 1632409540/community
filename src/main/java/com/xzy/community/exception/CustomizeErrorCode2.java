package com.xzy.community.exception;

public enum CustomizeErrorCode2 implements ICustomizeErrorCode{

    QUESTION_INSERT_ERROR("问题发布失败，要不要稍后再试试？");
    private String message;
    @Override
    public String getMessage(){
        return message;
    }
    CustomizeErrorCode2(String message){
        this.message=message;
    }

}
