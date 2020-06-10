package com.xzy.community.dto;

import com.xzy.community.model.User;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long notifier;
    private String notifierName;
    private String type;
    private String outerTitle;
    private Integer Status;
    private String gmtCreate;
}
