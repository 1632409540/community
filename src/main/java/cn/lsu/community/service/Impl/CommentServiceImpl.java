package cn.lsu.community.service.Impl;

import cn.lsu.community.base.BaseService;
import cn.lsu.community.dto.CommentDTO;
import cn.lsu.community.entity.Comment;
import cn.lsu.community.entity.Notification;
import cn.lsu.community.entity.Question;
import cn.lsu.community.entity.User;
import cn.lsu.community.exception.CustomizeErrorCode;
import cn.lsu.community.exception.CustomizeException;
import cn.lsu.community.mapper.CommentMapper;
import cn.lsu.community.mapper.NotificationMapper;
import cn.lsu.community.mapper.QuestionMapper;
import cn.lsu.community.enums.CommentTypeEnum;
import cn.lsu.community.enums.NotificationStatusEnum;
import cn.lsu.community.enums.NotificationTypeEnum;
import cn.lsu.community.mapper.UserMapper;
import cn.lsu.community.service.CommentService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends BaseService<CommentMapper,Comment> implements CommentService {

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private NotificationMapper notificationMapper;

    @Transactional
    public void insert(Comment comment, User sessionUser) {

        if(comment.getParentId()==null||comment.getParentId()==0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUNT);
        }

        if(comment.getType()==null||!CommentTypeEnum.isExit(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        if(comment.getType()==CommentTypeEnum.COMMENT.getType()){
            //回复评论
            Comment dbComment= baseMapper.selectById(comment.getParentId());
            if(dbComment==null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            baseMapper.insert(comment);
            dbComment.setCommentCount(1);
            baseMapper.addCommentCount(dbComment);
            //回复的问题
            Question question=questionMapper.selectById(dbComment.getParentId());

            if(question==null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            //创建通知
            createNotify(comment,NotificationTypeEnum.REPLAY_COMMENT.getType(),dbComment.getCommentator(),sessionUser.getName(),question.getId(),question.getTitle());

        }else {
            //回复问题
            Question question=questionMapper.selectById(comment.getParentId());
            if(question==null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            baseMapper.insert(comment);
            question.setCommentCount(1);
            questionMapper.addCommentCount(question);
            //创建通知
            createNotify(comment,NotificationTypeEnum.REPLAY_QUESTION.getType(),question.getCreator(),sessionUser.getName(),question.getId(),question.getTitle());
        }

    }
    private void createNotify(Comment comment,Integer type,Long reciver,String notifier,Long questionId,String title){
        if(comment.getCommentator().longValue()==reciver.longValue()){
            return;
        }
        Notification notification=new Notification();
        notification.setType(type);
        notification.setReceiver(reciver);
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNRead.getStatus());
        notification.setNotifierName(notifier);
        notification.setOuterId(questionId);
        notification.setOuterTitle(title);

        notificationMapper.insert(notification);
    }
    public List<CommentDTO> findCommentsById(Long id, CommentTypeEnum type) {

        Wrapper<Comment> wrapper = new EntityWrapper<>();
        wrapper.eq("parent_id", id)
                .eq("type", type.getType())
                .orderBy("create_date", false);
        List<Comment> comments=baseMapper.selectList(wrapper);
        if(comments.size()==0){
            return new LinkedList<>();
        }
        //获取去重的评论人
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds=new ArrayList<>();
        userIds.addAll(commentators);

        //获取评论人并转换为Map
        List<User> users = userMapper.selectBatchIds(userIds);
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
        Comment comment = baseMapper.selectById(id);
        if(comment!=null){
            comment.setLikeCount(1);
            baseMapper.addLikeCount(comment);
        }
        comment=baseMapper.selectById(id);
        return comment.getLikeCount();
    }
}
