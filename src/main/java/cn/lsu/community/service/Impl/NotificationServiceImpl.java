package cn.lsu.community.service.Impl;

import cn.lsu.community.base.BaseService;
import cn.lsu.community.dto.NotificationDTO;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.entity.Comment;
import cn.lsu.community.entity.Notification;
import cn.lsu.community.entity.User;
import cn.lsu.community.enums.NotificationType;
import cn.lsu.community.exception.CustomizeErrorCode;
import cn.lsu.community.exception.CustomizeException;
import cn.lsu.community.enums.NotificationStatusEnum;
import cn.lsu.community.mapper.NotificationMapper;
import cn.lsu.community.mapper.UserMapper;
import cn.lsu.community.service.NotificationService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class NotificationServiceImpl extends BaseService<NotificationMapper,Notification> implements NotificationService {


    public PaginationDTO list(User user, Integer page, Integer size) {

        PaginationDTO<NotificationDTO> paginationDTO=new PaginationDTO<>();
        Integer offSize=size*(page-1);

        Wrapper<Notification> wrapper =new EntityWrapper<>();
        wrapper.eq("receiver",user.getId())
                .orderBy("status",true)
                .orderBy("create_date",false);
        List<Notification> notifications = baseMapper.selectPage(new RowBounds(offSize, size), wrapper);
        if(notifications.size()<=0){
            return paginationDTO;
        }
        List<NotificationDTO> notificationDTOList=new LinkedList<>();
        for(Notification notification:notifications){
            NotificationDTO notificationDTO=new NotificationDTO();
            BeanUtils.copyProperties(notification,notificationDTO);
            notificationDTO.setType(NotificationType.getNotificationType(notification.getType()));
            notificationDTOList.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOList);
        Integer totalCount=baseMapper.selectCount(wrapper);
        paginationDTO.setPagination(totalCount,page,size);
        return paginationDTO;
    }

    public Long getUnReadCount(Long id) {
        Wrapper<Notification> wrapper =new EntityWrapper<>();
        wrapper.eq("receiver",id)
                .eq("status",NotificationStatusEnum.UNRead.getStatus());

        long count = baseMapper.selectCount(wrapper);
        return count;
    }

    public Notification viewNotificationById(User user, Long notificationId) {
        Notification notification=baseMapper.selectById(notificationId);
        if(notification==null){
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if(notification.getReceiver().longValue()!=user.getId().longValue()){
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_YOUR);
        }
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        baseMapper.updateById(notification);
        return notification;
    }

    public void createCommentNotify(Comment comment, Integer type, Long reciver, String notifier, Long questionId, String title) {
        if (comment.getCommentator().longValue() == reciver.longValue()) {
            return;
        }
        Notification notification = new Notification();
        notification.setType(type);
        notification.setReceiver(reciver);
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNRead.getStatus());
        notification.setNotifierName(notifier);
        notification.setOuterId(questionId);
        notification.setOuterTitle(title);
        notification.setCreateDate(new Date());
        baseMapper.insert(notification);
    }
}
