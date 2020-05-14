package com.xzy.community.controller;

import com.xzy.community.dto.CommentDTO;
import com.xzy.community.dto.ResultDTO;
import com.xzy.community.exception.CustomizeErrorCode;
import com.xzy.community.mapper.CommentMapper;
import com.xzy.community.mapper.QuestionAddCountMapper;
import com.xzy.community.model.Comment;
import com.xzy.community.model.User;
import com.xzy.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ResponseBody
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentDTO commentDTO,
                       HttpServletRequest request) {

        User user= (User) request.getSession().getAttribute("user");
        if(user==null){
            return ResultDTO.errorOf(CustomizeErrorCode.NOT_LOGIN);
        }
        Comment comment = new Comment();
        comment.setParentId(commentDTO.getParentId());
        comment.setContent(commentDTO.getContent());
        comment.setType(commentDTO.getType());
        comment.setCommentator(1L);
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());
        comment.setLikeCount(0);
        commentService.insert(comment);
        return ResultDTO.successOf();
    }
}
