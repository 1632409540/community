package cn.lsu.community.mapper;

import cn.lsu.community.base.BaseMapper;
import cn.lsu.community.dto.HotTopicDTO;
import cn.lsu.community.entity.Comment;
import cn.lsu.community.entity.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedList;
import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {
    LinkedList<HotTopicDTO> selectHotTopic();

    List<Tag> selectByQuestionId(@Param("questionId") Long questionId);

}