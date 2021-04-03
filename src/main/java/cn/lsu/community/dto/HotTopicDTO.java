package cn.lsu.community.dto;

import cn.lsu.community.entity.Tag;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.FieldFill;
import lombok.Data;

import java.util.Date;
import java.util.LinkedList;

@Data
public class HotTopicDTO extends Tag{

    private Long tag_type_id;
    private String name;
    private String imageUrl;
    private int questionCount;
    private int likeCount;
    private boolean myLike;
    private int weekCount;
    private int monthCount;
    private Long id;
    private Date createDate;
    private Date lastModified;
}
