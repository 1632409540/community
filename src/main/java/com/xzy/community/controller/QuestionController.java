package com.xzy.community.controller;

import com.xzy.community.dto.CommentDTO;
import com.xzy.community.dto.PaginationDTO;
import com.xzy.community.dto.QuestionDTO;
import com.xzy.community.enums.CommentTypeEnum;
import com.xzy.community.model.Question;
import com.xzy.community.service.CommentService;
import com.xzy.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id")Long id,
                           @RequestParam(name = "page",defaultValue = "1")Integer page,
                           @RequestParam(name = "size",defaultValue = "11")Integer size,
                            Model model){

        QuestionDTO questionDTO=questionService.findById(id);
        List<CommentDTO> commentDTOList =commentService.findCommentsById(id, CommentTypeEnum.QUESTION);
        model.addAttribute("question",questionDTO);
        String[] tags=questionDTO.getTag().split(",");
        model.addAttribute("tags",tags);
        model.addAttribute("comments",commentDTOList);
        PaginationDTO paginationDTO= questionService.list(id, null,questionDTO.getTag(),page,size);
        model.addAttribute("likeQuestions",paginationDTO);
        questionService.addViewCount(id);
        return "question";
    }

}
