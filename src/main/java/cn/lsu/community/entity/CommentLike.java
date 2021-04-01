package cn.lsu.community.entity;

import cn.lsu.community.base.BaseEntity;

public class CommentLike extends BaseEntity<CommentLike> {

    private Long commentId;

    private Long userId;

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
