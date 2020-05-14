package com.xzy.community.mapper;

import com.xzy.community.model.Question;
import com.xzy.community.model.QuestionExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface QuestionAddCountMapper {

    int addViewCount(Question record);
    int addCommentCount(Question question);

}