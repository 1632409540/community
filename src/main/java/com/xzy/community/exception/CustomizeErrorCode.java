package com.xzy.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode{

    QUESTION_NOT_FOUND(2001,"你找的问题不存在，要不要换一个试试？"),
    TARGET_PARAM_NOT_FOUNT(2003,"未选中任何问题或评论进行回复！"),
    QUESTION_INSERT_ERROR(2002,"问题发布失败，要不要稍后再试试？"),
    WRITE_RESULT_ERROR(2008,"返回JSON数据失败!"),
    NOT_LOGIN(2004,"当前操作需要登录，是否请求登录？"),
    TYPE_PARAM_WRONG(2006,"评论类型错误或不存在！"),
    SEND_AJAX_ERROR(2009,"发送AjarPOST请求失败！"),
    LOGIN_FAULT_ERROR(2010,"登录失败，请稍后试试！"),
    REQUEST_ERROR(2011,"你的请求出错了吧，要不然换个姿势？"),
    COMMENT_NOT_FOUND(2007,"你回复的评论不存在，要不要换一个试试？"),
    SYSTEM_ERROR(2005,"服务器冒烟了，请稍后再试试！！！");
    private String message;
    private Integer code;
    @Override
    public String getMessage(){
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }
}
