package cn.lsu.community.mapper;

import cn.lsu.community.base.BaseMapper;
import cn.lsu.community.entity.Comment;
import cn.lsu.community.entity.User;

public interface CommentMapper extends BaseMapper<Comment> {
    int addLikeCount(Comment comment);
    int addCommentCount(Comment comment);
}