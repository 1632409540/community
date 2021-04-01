package cn.lsu.community.entity;

import cn.lsu.community.base.BaseEntity;

public class QuestionLike extends BaseEntity<QuestionLike> {

    private Long questionId;

    private Long userId;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
