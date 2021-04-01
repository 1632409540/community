package cn.lsu.community.entity;

import cn.lsu.community.base.BaseEntity;

public class QuestionTag extends BaseEntity<QuestionTag> {

    private Long questionId;

    private Long tagId;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}
