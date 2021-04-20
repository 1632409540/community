package cn.lsu.community.service.Impl;

import cn.lsu.community.base.BaseService;
import cn.lsu.community.dto.CommentDTO;
import cn.lsu.community.entity.*;
import cn.lsu.community.enums.NotificationType;
import cn.lsu.community.exception.CustomizeErrorCode;
import cn.lsu.community.exception.CustomizeException;
import cn.lsu.community.mapper.*;
import cn.lsu.community.enums.CommentTypeEnum;
import cn.lsu.community.enums.NotificationStatusEnum;
import cn.lsu.community.service.CommentService;
import cn.lsu.community.service.NotificationService;
import cn.lsu.community.service.QuestionService;
import cn.lsu.community.service.UserService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends BaseService<CommentMapper, Comment> implements CommentService {

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private NotificationService notificationService;

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private CommentLikeMapper commentLikeMapper;

    @Resource
    private CommentMapper commentMapper;

    @Transactional
    public void insert(Comment comment, User sessionUser) {

        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUNT);
        }

        if (comment.getType() == null || !CommentTypeEnum.isExit(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            //回复评论
            Comment dbComment = baseMapper.selectById(comment.getParentId());
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            comment.setCreateDate(new Date());
            baseMapper.insert(comment);
            dbComment.setCommentCount(1);
            baseMapper.addCommentCount(dbComment);
            //回复的问题
            Question question = questionMapper.selectById(dbComment.getParentId());

            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            //创建通知，回复评论
            notificationService.createCommentNotify(comment, NotificationType.REPLAY_COMMENT.getType(), dbComment.getCommentator(), sessionUser.getName(), question.getId(), comment.getContent());
            User addUser = userMapper.selectById(dbComment.getCommentator());
            userService.updateUserIntegral(addUser,sessionUser,10);
        } else {
            //回复问题
            Question question = questionMapper.selectById(comment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            comment.setCreateDate(new Date());
            baseMapper.insert(comment);
            question.setCommentCount(1);
            questionMapper.addCommentCount(question);
            //创建通知，回复问题
            notificationService.createCommentNotify(comment, NotificationType.REPLAY_QUESTION.getType(), question.getCreator(), sessionUser.getName(), question.getId(), question.getTitle());
            User addUser = userMapper.selectById(question.selectById());
            userService.updateUserIntegral(addUser,sessionUser,10);
        }

    }



    public List<CommentDTO> findCommentsById(User currentUser,Long id, CommentTypeEnum type) {

        Wrapper<Comment> wrapper = new EntityWrapper<>();
        wrapper.eq("parent_id", id);
        List<Comment> comments = baseMapper.selectList(wrapper);
        if (ObjectUtils.isEmpty(comments)|| comments.size() == 0) {
            return new LinkedList<>();
        }
        //获取去重的评论人
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);

        //获取评论人并转换为Map
        List<User> users = userMapper.selectBatchIds(userIds);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        //转换comment为commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            Wrapper<Comment> commentWrapper = new EntityWrapper<>();
            commentWrapper.eq("parent_id", comment.getId());
            commentDTO.setCommentCount(commentMapper.selectCount(commentWrapper));
            commentDTO.setMyLike(false);
            if(ObjectUtils.isNotEmpty(currentUser)){
                Wrapper<CommentLike> wrapper1 = new EntityWrapper<>();
                wrapper1.eq("comment_id",comment.getId());
                commentDTO.setLikeCount(commentLikeMapper.selectCount(wrapper1));
                wrapper1.andNew().eq("user_id",currentUser.getId());
                Integer count = commentLikeMapper.selectCount(wrapper1);
                if(count>0){
                    commentDTO.setMyLike(true);
                }
            }
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }

    @Override
    public Integer changeLikeCount(User user, Long id) {
        Wrapper<CommentLike> wrapper1 = new EntityWrapper<>();
        wrapper1.eq("comment_id",id)
                .eq("user_id", user.getId());
        Integer count = commentLikeMapper.selectCount(wrapper1);
        if(count>0){
            commentLikeMapper.delete(wrapper1);
        }else {
            CommentLike commentLike = new CommentLike();
            commentLike.setCommentId(id);
            commentLike.setUserId(user.getId());
            commentLikeMapper.insert(commentLike);
            Comment comment = commentMapper.selectById(id);
            User addUser = userMapper.selectById(comment.getCommentator());
            userService.updateUserIntegral(addUser,user,5);
        }
        Wrapper<CommentLike> wrapper2 = new EntityWrapper<>();
        wrapper1.eq("comment_id",id);
        return commentLikeMapper.selectCount(wrapper2);
    }
}
