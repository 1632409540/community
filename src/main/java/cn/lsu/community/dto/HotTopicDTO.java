package cn.lsu.community.dto;

import cn.lsu.community.entity.Tag;
import lombok.Data;

import java.util.LinkedList;

@Data
public class HotTopicDTO extends Tag{
    private int questionCount;
    private int likeCount;
    private boolean myLike;
}
