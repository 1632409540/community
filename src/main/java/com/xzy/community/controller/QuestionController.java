package com.xzy.community.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.xzy.community.dto.QuestionDTO;
import com.xzy.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id")Integer id,
                            Model model){
        questionService.addViewCount(id);
        QuestionDTO questionDTO=questionService.findById(id);
        model.addAttribute("question",questionDTO);
        return "question";
    }

}
