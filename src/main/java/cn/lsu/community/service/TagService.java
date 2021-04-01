package cn.lsu.community.service;

import cn.lsu.community.entity.Tag;
import cn.lsu.community.entity.TagType;
import cn.lsu.community.entity.User;

import java.util.List;

public interface TagService {

    List<TagType> getTagTypes();

    List<Tag> filterInvalid(String tag);

    Tag selectByName(String tag);

    Tag selectById(Long tagId);
}
