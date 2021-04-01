package cn.lsu.community.service.Impl;

import cn.lsu.community.base.BaseService;
import cn.lsu.community.entity.Tag;
import cn.lsu.community.entity.TagType;
import cn.lsu.community.entity.User;
import cn.lsu.community.mapper.TagMapper;
import cn.lsu.community.mapper.TagTypeMapper;
import cn.lsu.community.mapper.UserMapper;
import cn.lsu.community.service.TagService;
import cn.lsu.community.service.UserService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl extends BaseService<TagMapper, Tag> implements TagService {

    @Resource
    private TagTypeMapper tagTypeMapper;

    @Override
    public List<TagType> getTagTypes() {
        List<TagType> list=tagTypeMapper.selectList(new EntityWrapper<>());
        list.forEach(tagType -> {
            Wrapper<Tag> wrapper = new EntityWrapper<>();
            wrapper.eq("tag_type_id",tagType.getId());
            List<Tag> tags = baseMapper.selectList(wrapper);
            tagType.setTags(tags);
        });
        return list;
    }

    @Override
    public  List<Tag> filterInvalid(String tagNames){
        List<Tag> tags = new ArrayList<>();
        String[] names = tagNames.split(",");
        for (String name: names) {
            Tag tag = this.selectByName(name);
            if(ObjectUtils.isNotEmpty(tag)){
                tags.add(tag);
            }else {
                return null;
            }
        }
        return tags;
    }

    @Override
    public Tag selectByName(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return baseMapper.selectOne(tag);
    }

    @Override
    public Tag selectById(Long tagId) {
        return baseMapper.selectById(tagId);
    }
}
