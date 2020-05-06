package com.xzy.community.dto;

import com.xzy.community.model.User;
import lombok.Data;

@Data
public class QuestionDTO {
    private int id;
    private String title ;
    private String description;
    private  Long gmtCreate;
    private Long gmtModified;
    private Integer creator;
    private Integer comuntCount;
    private Integer viewCount ;
    private Integer likeCount;
    private String tag ;
    private User user;
}
