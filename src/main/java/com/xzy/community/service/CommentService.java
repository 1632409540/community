package com.xzy.community.service;

import com.xzy.community.dto.CommentDTO;
import com.xzy.community.enums.CommentTypeEnum;
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

    @Transactional
    public void insert(Comment comment) {
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

        }else {
            //回复问题
            Question question=questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question==null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionAddCountMapper.addCommentCount(question);
        }
    }

    public List<CommentDTO> findCommentsById(Long id, CommentTypeEnum type) {
        CommentExample example=new CommentExample();
        example.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        example.setOrderByClause("gmt_create desc");
        List<Comment> comments=commentMapper.selectByExample(example);

//        List<CommentDTO> commentDTOS=new LinkedList<>();
//        for (Comment comment: comments) {
//            CommentDTO commentDTO=new CommentDTO();
//            BeanUtils.copyProperties(comment,commentDTO);
//            commentDTO.setUser(userMapper.selectByPrimaryKey(comment.getCommentator()));
//            commentDTOS.add(commentDTO);
//        }

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
}
