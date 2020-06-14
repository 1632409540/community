package com.xzy.community.service;

import com.xzy.community.dto.CommentDTO;
import com.xzy.community.enums.CommentTypeEnum;
import com.xzy.community.enums.NotificationStatusEnum;
import com.xzy.community.enums.NotificationTypeEnum;
import com.xzy.community.exception.CustomizeErrorCode;
import com.xzy.community.exception.CustomizeException;
import com.xzy.community.mapper.*;
import com.xzy.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionAddCountMapper questionAddCountMapper;
    @Autowired
    private CommentAddCountMapper commentAddCountMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional
    public void insert(Comment comment,User sessionUser) {

        if(comment.getParentId()==null||comment.getParentId()==0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUNT);
        }

        if(comment.getType()==null||!CommentTypeEnum.isExit(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        if(comment.getType()==CommentTypeEnum.COMMENT.getType()){
            //回复评论
            Comment dbComment= commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment==null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
            dbComment.setCommentCount(1);
            commentAddCountMapper.addCommentCount(dbComment);
            //回复的问题
            Question question=questionMapper.selectByPrimaryKey(dbComment.getParentId());

            if(question==null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            //创建通知
            createNotify(comment,NotificationTypeEnum.REPLAY_COMMENT.getType(),dbComment.getCommentator(),sessionUser.getName(),question.getId(),question.getTitle());

        }else {
            //回复问题
            Question question=questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question==null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionAddCountMapper.addCommentCount(question);
            //创建通知
            createNotify(comment,NotificationTypeEnum.REPLAY_QUESTION.getType(),question.getCreator(),sessionUser.getName(),question.getId(),question.getTitle());
        }

    }
    private void createNotify(Comment comment,Integer type,Long reciver,String notifier,Long questionId,String title){
        if(comment.getCommentator()==reciver){
            return;
        }
        Notification notification=new Notification();
        notification.setType(type);
        notification.setReceiver(reciver);
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNRead.getStatus());
        notification.setNotifierName(notifier);
        notification.setOuterId(questionId);
        notification.setOuterTitle(title);

        notificationMapper.insert(notification);
    }
    public List<CommentDTO> findCommentsById(Long id, CommentTypeEnum type) {
        CommentExample example=new CommentExample();
        example.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        example.setOrderByClause("gmt_create desc");
        List<Comment> comments=commentMapper.selectByExample(example);

        if(comments.size()==0){
            return new LinkedList<>();
        }
        //获取去重的评论人
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds=new ArrayList<>();
        userIds.addAll(commentators);

        //获取评论人并转换为Map
        UserExample userExample=new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        //转换comment为commentDTO
        List<CommentDTO> commentDTOS=comments.stream().map(comment -> {
          CommentDTO commentDTO=new CommentDTO();
          BeanUtils.copyProperties(comment,commentDTO);
          commentDTO.setUser(userMap.get(comment.getCommentator()));
          return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }

    public Integer addLikeCount(Long id) {
        Comment comment = commentMapper.selectByPrimaryKey(id);
        if(comment!=null){
            comment.setLikeCount(1);
            commentAddCountMapper.addLikeCount(comment);
        }
        comment=commentMapper.selectByPrimaryKey(id);
        return comment.getLikeCount();
    }
}
