package com.xzy.community.dto;

import com.xzy.community.model.User;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long notifier;
    private String notifierName;
    private String type;
    private Long outerId;
    private String outerTitle;
    private Long gmtCreate;
    private Integer status;

}
