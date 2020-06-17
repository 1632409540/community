package com.xzy.community.mapper;

import com.xzy.community.dto.QuestionQueryDTO;
import com.xzy.community.model.Comment;
import com.xzy.community.model.Question;
import com.xzy.community.model.QuestionExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface QuestionAddCountMapper {

    int addViewCount(Question record);
    int addCommentCount(Question question);

    Integer countBySearch(QuestionQueryDTO questionQueryDTO);

    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);
}