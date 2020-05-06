package com.xzy.community.dto;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;
@Data
public class PaginationDTO {
    private List<QuestionDTO> questions;
    private Boolean showPrevious;
    private Boolean showFirstPage;
    private Boolean showNextPage;
    private Boolean showEndPage;
    private Integer totalPage;
    private Integer page;
    private List<Integer> pages =new LinkedList<>();

    public void setPagination(Integer totalCount, Integer page, Integer size) {
        this.page=page;
        if(totalCount%size==0){
            totalPage=totalCount/size;
        }else {
            totalPage=totalCount/size+1;
        }
        pages.clear();
        pages.add(page);
        for (int i = 1; i <=3 ; i++) {
            if(page-i>0){
                pages.add(0,page-i);
            }
            if(page+i<=totalPage){
                pages.add(page+i);
            }
        }

        //是否显示首页
        if(page>=5){
            showFirstPage=true;
        }else {
            showFirstPage=false;
        }
        //是否显示尾页
        if(page<=totalPage-5){
            showEndPage=true;
        }else {
            showEndPage=false;
        }
        //是否显示上一页
        if(page>1){
            showPrevious=true;
        }else {
            showPrevious=false;
        }
        //是否显示下一页
        if(page<totalPage){
            showNextPage=true;
        }else {
            showNextPage=false;
        }
    }
}
