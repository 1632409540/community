package cn.lsu.community.service.Impl;

import cn.lsu.community.base.BaseService;
import cn.lsu.community.dto.CommentDTO;
import cn.lsu.community.dto.PaginationDTO;
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
import cn.lsu.community.utils.MailUtils;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.net.InetAddress;
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

    @Resource
    private QuestionLikeMapper questionLikeMapper;

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
            User addUser = userMapper.selectById(dbComment.getCommentator());
            userService.updateUserIntegral(addUser, sessionUser, 10);
            if (addUser.getNotifiComment() == 1) {
                notificationService.createCommentNotify(comment, NotificationType.REPLAY_COMMENT.getType(), dbComment.getCommentator(), sessionUser.getName(), question.getId(), comment.getContent());
            }
            if (addUser.getEmailComment() == 1) {
                try {
                    //邮件发送正文
                    InetAddress addr = null;
                    String context = sessionUser.getName() + NotificationType.REPLAY_COMMENT.getName() + "<a href='http://" + addr.getHostAddress()
                            + ":8090/question/" + question.getId() + "'>" + dbComment.getContent() + "</a>";
                    //发送邮件
                    MailUtils.sendMail(addUser.getEmail(), context, "校园博客系统-通知");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
            User addUser = userMapper.selectById(question.getCreator());
            userService.updateUserIntegral(addUser, sessionUser, 10);
            if (addUser.getNotifiAnswer() == 1) {
                notificationService.createCommentNotify(comment, NotificationType.REPLAY_QUESTION.getType(), question.getCreator(), sessionUser.getName(), question.getId(), question.getTitle());
            }
            if (addUser.getEmailAnswer() == 1) {
                try {
                    //邮件发送正文
                    InetAddress addr = null;
                    String context = sessionUser.getName() + NotificationType.REPLAY_QUESTION.getName() + "<a href='http://" + addr.getHostAddress()
                            + ":8090/question/" + question.getId() + "'>" + question.getTitle() + "</a>";
                    //发送邮件
                    MailUtils.sendMail(addUser.getEmail(), context, "校园博客系统-通知");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Wrapper<QuestionLike> questionLikeWrapper = new EntityWrapper<>();
            questionLikeWrapper.eq("question_id", question.getId());
            List<QuestionLike> questionLikes = questionLikeMapper.selectList(questionLikeWrapper);
            questionLikes.stream().forEach(questionLike -> {
                User pUser = userMapper.selectById(questionLike.getUserId());
                if (pUser.getNotifiNewAnswer() == 1) {
                    notificationService.createCommentNotify(comment, NotificationType.YOUR_LIKED_QUESTION_HAVE_NEW_COMMENT.getType(), question.getCreator(), sessionUser.getName(), question.getId(), question.getTitle());
                }
                if (sessionUser.getId().longValue() != pUser.getId().longValue() && pUser.getEmailNewAnswer() == 1) {
                    try {
                        //邮件发送正文
                        InetAddress addr = null;
                        String context = NotificationType.YOUR_LIKED_QUESTION_HAVE_NEW_COMMENT.getName() + "<a href='http://" + addr.getHostAddress()
                                + ":8090/question/" + question.getId() + "'>" + question.getTitle() + "</a>";
                        //发送邮件
                        MailUtils.sendMail(pUser.getEmail(), context, "校园博客系统-通知");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public List<CommentDTO> findCommentsById(User currentUser, Long id, CommentTypeEnum type) {

        Wrapper<Comment> wrapper = new EntityWrapper<>();
        wrapper.eq("parent_id", id);
        List<Comment> comments = baseMapper.selectList(wrapper);
        if (ObjectUtils.isEmpty(comments) || comments.size() == 0) {
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
            if (ObjectUtils.isNotEmpty(currentUser)) {
                Wrapper<CommentLike> wrapper1 = new EntityWrapper<>();
                wrapper1.eq("comment_id", comment.getId());
                commentDTO.setLikeCount(commentLikeMapper.selectCount(wrapper1));
                wrapper1.andNew().eq("user_id", currentUser.getId());
                Integer count = commentLikeMapper.selectCount(wrapper1);
                if (count > 0) {
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
        wrapper1.eq("comment_id", id)
                .eq("user_id", user.getId());
        Integer count = commentLikeMapper.selectCount(wrapper1);
        if (count > 0) {
            commentLikeMapper.delete(wrapper1);
        } else {
            CommentLike commentLike = new CommentLike();
            commentLike.setCommentId(id);
            commentLike.setUserId(user.getId());
            commentLikeMapper.insert(commentLike);
            Comment comment = commentMapper.selectById(id);
            User addUser = userMapper.selectById(comment.getCommentator());
            userService.updateUserIntegral(addUser, user, 5);
        }
        Wrapper<CommentLike> wrapper2 = new EntityWrapper<>();
        wrapper1.eq("comment_id", id);
        return commentLikeMapper.selectCount(wrapper2);
    }

    @Override
    public PaginationDTO queryAll() {

        Wrapper<Comment> commentWrapper = new EntityWrapper<>();
        commentWrapper.orderBy("create_date", false);
        List<Comment> commentList = commentMapper.selectList(commentWrapper);
        List<CommentDTO> commentDTOS = new ArrayList<>();
        commentList.forEach(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMapper.selectById(comment.getCommentator()));
            if (comment.getType() == 1) {
                commentDTO.setQuestion(questionMapper.selectById(comment.getParentId()));
            } else if (comment.getType() == 2) {
                Comment comment1 = commentMapper.selectById(comment.getParentId());
                commentDTO.setQuestion(questionMapper.selectById(comment1.getParentId()));
            }
            commentDTOS.add(commentDTO);
        });
        PaginationDTO<CommentDTO> paginationDTO = new PaginationDTO<CommentDTO>();
        paginationDTO.setData(commentDTOS);
        paginationDTO.setPagination(commentDTOS.size(), 1, 1);
        return paginationDTO;
    }

    @Override
    public Integer deleteCommentById(Long id) {
        return baseMapper.deleteById(id);
    }
}
