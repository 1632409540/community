package com.xzy.community.mapper;

import com.xzy.community.model.Comment;


public interface CommentExtMapper {

    int addLikeCount(Comment comment);
    int addCommentCount(Comment comment);
}