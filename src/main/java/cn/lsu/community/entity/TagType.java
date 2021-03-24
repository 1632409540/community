package cn.lsu.community.entity;

import cn.lsu.community.base.BaseEntity;

import java.util.List;

public class TagType extends BaseEntity<TagType> {

    private String name;

    private List<Tag> tags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
