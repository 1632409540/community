package cn.lsu.community.mapper;

import cn.lsu.community.base.BaseMapper;
import cn.lsu.community.dto.HotTopicDTO;
import cn.lsu.community.entity.Comment;
import cn.lsu.community.entity.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {
    LinkedList<HotTopicDTO> selectHotTopic();

    List<Tag> selectByQuestionId(@Param("questionId") Long questionId);

    Integer selectCountBySearch(@Param("search") String search);

    List<HotTopicDTO> selectPageBySearch(@Param("search") String search,
                                         @Param("offSize") Integer offSize, @Param("size") Integer size);
}