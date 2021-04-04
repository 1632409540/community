package cn.lsu.community.dto;

import cn.lsu.community.enums.NotificationType;
import lombok.Data;

import java.util.Date;

@Data
public class NotificationDTO {
    private Long id;
    private Long notifier;
    private String notifierName;
    private NotificationType type;
    private Long outerId;
    private String outerTitle;
    private Date createDate;;
    private Date lastModified;
    private Integer status;

}
