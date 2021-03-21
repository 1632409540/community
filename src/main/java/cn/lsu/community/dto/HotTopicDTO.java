package cn.lsu.community.dto;

import lombok.Data;

import java.util.LinkedList;

@Data
public class HotTopicDTO {

    private LinkedList<HotTopicDTO> data;
    private String tag;
    private int questionCount;
    private int commentCount;
}
