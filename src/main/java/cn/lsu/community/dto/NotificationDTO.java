package cn.lsu.community.dto;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationDTO {
    private Long id;
    private Long notifier;
    private String notifierName;
    private String type;
    private Long outerId;
    private String outerTitle;
    private Date createDate;;
    private Date lastModified;
    private Integer status;

}
