package cn.lsu.community.entity;

import cn.lsu.community.base.BaseEntity;

public class TagLike extends BaseEntity<TagLike> {

    private Long tagId;

    private Long userId;

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
