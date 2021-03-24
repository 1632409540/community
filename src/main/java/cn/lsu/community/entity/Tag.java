package cn.lsu.community.entity;

import cn.lsu.community.base.BaseEntity;

public class Tag extends BaseEntity<Tag> {

    private Long tag_type_id;

    private String name;

    public Long getTag_type_id() {
        return tag_type_id;
    }

    public void setTag_type_id(Long tag_type_id) {
        this.tag_type_id = tag_type_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
