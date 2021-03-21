package cn.lsu.community.service;

import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.entity.Notification;
import cn.lsu.community.entity.User;

public interface NotificationService {
    public PaginationDTO list(User user, Integer page, Integer size);

    public Long getUnReadCount(Long id);

    public Notification viewNotificationById(User user, Long notificationId);
}
