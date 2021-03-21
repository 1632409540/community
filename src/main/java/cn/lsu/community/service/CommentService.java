package cn.lsu.community.service;

import cn.lsu.community.dto.CommentDTO;
import cn.lsu.community.entity.Comment;
import cn.lsu.community.entity.User;
import cn.lsu.community.enums.CommentTypeEnum;

import java.util.List;

public interface CommentService {
    public void insert(Comment comment, User sessionUser);

    public List<CommentDTO> findCommentsById(Long id, CommentTypeEnum type);

    public Integer addLikeCount(Long id);
}
