package cn.lsu.community.dto;

import lombok.Data;

@Data
public class QuestionQueryDTO {
    private Long id;
    private String search;
    private Integer size;
    private Integer page;
}
