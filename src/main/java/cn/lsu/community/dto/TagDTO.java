package cn.lsu.community.dto;

import lombok.Data;

import java.util.List;

@Data
public class TagDTO {
    private String id;
    private String categoryName;
    private List<String> tags;
}
