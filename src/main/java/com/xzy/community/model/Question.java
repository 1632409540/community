package com.xzy.community.model;

import lombok.Data;

@Data
public class Question {
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

}
