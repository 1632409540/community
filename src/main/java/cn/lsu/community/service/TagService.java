package cn.lsu.community.service;

import cn.lsu.community.dto.HotTopicDTO;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.entity.Tag;
import cn.lsu.community.entity.TagType;
import cn.lsu.community.entity.User;

import java.util.List;

public interface TagService {

    List<TagType> getTagTypes();

    List<Tag> filterInvalid(String tag);

    Tag selectByName(String tag);

    HotTopicDTO selectById(User user, Long tagId);

    PaginationDTO list(String search, Integer page, Integer size);

    void changeLikeTag(User user, Long tagId);

    PaginationDTO queryAll();
}
