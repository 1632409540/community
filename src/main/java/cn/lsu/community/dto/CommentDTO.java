package cn.lsu.community.dto;

import cn.lsu.community.entity.User;
import lombok.Data;

import java.util.Date;

@Data
public class CommentDTO {

    private Long id;
    private Long parentId;
    private Integer type;
    private Long commentator;
    private Date createDate;;
    private Date lastModified;
    private Integer likeCount;
    private Integer commentCount;
    private String content;
    private User user;

}
