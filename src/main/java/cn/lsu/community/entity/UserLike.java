package cn.lsu.community.entity;

import cn.lsu.community.base.BaseEntity;

public class UserLike extends BaseEntity<UserLike> {

    private Long likedUserId;

    private Long userId;

    public Long getLikedUserId() {
        return likedUserId;
    }

    public void setLikedUserId(Long likedUserId) {
        this.likedUserId = likedUserId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
