package cn.lsu.community.service.Impl;

import cn.lsu.community.base.BaseService;
import cn.lsu.community.dto.HotTopicDTO;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.dto.QuestionDTO;
import cn.lsu.community.dto.QuestionQueryDTO;
import cn.lsu.community.entity.*;
import cn.lsu.community.exception.CustomizeErrorCode;
import cn.lsu.community.exception.CustomizeException;
import cn.lsu.community.mapper.TagLikeMapper;
import cn.lsu.community.mapper.TagMapper;
import cn.lsu.community.mapper.TagTypeMapper;
import cn.lsu.community.service.TagService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl extends BaseService<TagMapper, Tag> implements TagService {

    @Resource
    private TagTypeMapper tagTypeMapper;

    @Resource
    private TagLikeMapper tagLikeMapper;

    @Resource
    private TagMapper tagMapper;

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
    public HotTopicDTO selectById(User user, Long tagId) {
        Tag tag = baseMapper.selectById(tagId);
        HotTopicDTO hotTopicDTO = new HotTopicDTO();
        BeanUtils.copyProperties(tag,hotTopicDTO);
        if(ObjectUtils.isNotEmpty(user)){
            Wrapper<TagLike> wrapper = new EntityWrapper<>();
            wrapper.eq("tag_id",tagId)
                    .eq("user_id",user.getId());
            Integer count = tagLikeMapper.selectCount(wrapper);
            hotTopicDTO.setLikeCount(count);
            if(count>0){
                hotTopicDTO.setMyLike(true);
            }else {
                hotTopicDTO.setMyLike(false);
            }
        }
        return hotTopicDTO;
    }

    @Override
    public PaginationDTO list(String search, Integer page, Integer size) {

        Integer offSize=size*(page-1);
        if(StringUtils.isNotEmpty(search)){
            String[] searchs=search.split(" ");
            search= Arrays.stream(searchs).collect(Collectors.joining("|"));
        }
        Integer totalCount= tagMapper.selectCountBySearch(search);
        List<HotTopicDTO> tagDTOList= tagMapper.selectPageBySearch(search,offSize,size);
        PaginationDTO<HotTopicDTO> paginationDTO=new PaginationDTO<HotTopicDTO>();
        paginationDTO.setData(tagDTOList);
        paginationDTO.setPagination(totalCount,page,size);
        return paginationDTO;
    }

    @Override
    public void changeLikeTag(User user, Long tagId) {
        if(ObjectUtils.isEmpty(user)){
            throw new CustomizeException(CustomizeErrorCode.NOT_LOGIN);
        }
        Wrapper<TagLike> tagLikeWrapper = new EntityWrapper<>();
        tagLikeWrapper.eq("user_id",user.getId())
                .eq("tag_id",tagId);
        Integer count = tagLikeMapper.selectCount(tagLikeWrapper);
        if(count>0){
            tagLikeMapper.delete(tagLikeWrapper);
        }else {
            TagLike tagLike = new TagLike();
            tagLike.setUserId(user.getId());
            tagLike.setTagId(tagId);
            tagLikeMapper.insert(tagLike);
        }
    }
}
