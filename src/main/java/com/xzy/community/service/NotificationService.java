package com.xzy.community.service;

import com.xzy.community.dto.NotificationDTO;
import com.xzy.community.dto.PaginationDTO;
import com.xzy.community.dto.QuestionDTO;
import com.xzy.community.enums.NotificationTypeEnum;
import com.xzy.community.mapper.NotificationMapper;
import com.xzy.community.mapper.UserMapper;
import com.xzy.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(User user, Integer page, Integer size) {

        PaginationDTO<NotificationDTO> paginationDTO=new PaginationDTO<>();
        Integer offSize=size*(page-1);
        NotificationExample example=new NotificationExample();
        example.createCriteria().andReceiverEqualTo(user.getId());
        example.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example, new RowBounds(offSize, size));
        List<NotificationDTO> notificationDTOList=new LinkedList<>();

        for(Notification notification:notifications){
            NotificationDTO notificationDTO=new NotificationDTO();
            BeanUtils.copyProperties(notification,notificationDTO);
            notificationDTO.setType(NotificationTypeEnum.getName(notification.getType()));
            notificationDTOList.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOList);

        NotificationExample example2=new NotificationExample();
        example.createCriteria().andReceiverEqualTo(user.getId());
        Integer totalCount=(int)notificationMapper.countByExample(example);
        paginationDTO.setPagination(totalCount,page,size);
        return paginationDTO;
    }
}
