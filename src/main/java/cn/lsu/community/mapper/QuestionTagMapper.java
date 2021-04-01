package cn.lsu.community.mapper;

import cn.lsu.community.base.BaseMapper;
import cn.lsu.community.dto.HotTopicDTO;
import cn.lsu.community.entity.Question;
import cn.lsu.community.entity.QuestionTag;
import cn.lsu.community.entity.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedList;
import java.util.List;

public interface QuestionTagMapper extends BaseMapper<QuestionTag> {

}