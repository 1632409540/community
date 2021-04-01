package cn.lsu.community.mapper;

import cn.lsu.community.base.BaseMapper;
import cn.lsu.community.dto.QuestionQueryDTO;
import cn.lsu.community.entity.Question;
import cn.lsu.community.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuestionMapper extends BaseMapper<Question> {

    int addViewCount(Question record);

    int addCommentCount(Question question);

    Integer selectCountByTagAndSearch(@Param("tagId") Long tagId,@Param("search") String search);

    List<Question> selectPageByTagAndSearch(@Param("tagId") Long tagId,@Param("search") String search,@Param("offSize") Integer offSize,@Param("size") Integer size);

    void addLikeCount(@Param("question") Question question);
}