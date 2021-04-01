package cn.lsu.community.dto;

import cn.lsu.community.entity.Tag;
import cn.lsu.community.entity.User;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class QuestionDTO {
    private Long id;
    private String title ;
    private String description;
    private Date createDate;;
    private Date lastModified;
    private Long creator;
    private Integer commentCount;
    private Integer viewCount ;
    private Integer likeCount;
    private String tag;
    private List<Tag> tags ;
    private User user;
    private Integer status;
    private boolean myLike;
}
