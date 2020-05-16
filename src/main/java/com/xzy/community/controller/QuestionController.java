package com.xzy.community.controller;

import com.xzy.community.dto.CommentCreateDTO;
import com.xzy.community.dto.CommentDTO;
import com.xzy.community.dto.QuestionDTO;
import com.xzy.community.service.CommentService;
import com.xzy.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id")Long id,
                            Model model){
        questionService.addViewCount(id);
        QuestionDTO questionDTO=questionService.findById(id);
        List<CommentDTO> commentDTOList =commentService.findCommentsById(id);
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",commentDTOList);
        return "question";
    }

}
