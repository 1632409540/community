package cn.lsu.community.dto;

import cn.lsu.community.entity.Tag;
import cn.lsu.community.entity.TagType;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.FieldFill;
import lombok.Data;

import java.util.Date;
import java.util.LinkedList;

@Data
public class HotTopicDTO extends Tag{

    private TagType tagType;
    private int questionCount;
    private int likeCount;
    private boolean myLike;
    private int weekCount;
    private int monthCount;
}
