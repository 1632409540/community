package com.xzy.community.dto;

import lombok.Data;

@Data
public class QuestionQueryDTO {
    private Long id;
    private String search;
    private String tag;
    private Integer size;
    private Integer page;
}
