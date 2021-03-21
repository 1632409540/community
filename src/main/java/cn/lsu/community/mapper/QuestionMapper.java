package cn.lsu.community.mapper;

import cn.lsu.community.base.BaseMapper;
import cn.lsu.community.dto.QuestionQueryDTO;
import cn.lsu.community.entity.Question;
import cn.lsu.community.entity.User;

import java.util.List;

public interface QuestionMapper extends BaseMapper<Question> {

    int addViewCount(Question record);
    int addCommentCount(Question question);

    Integer countBySearchAndTagExceptId(QuestionQueryDTO questionQueryDTO);

    List<Question> selectBySearchAndTagExceptId(QuestionQueryDTO questionQueryDTO);

    int getCommentCountByTag(String tag);
}