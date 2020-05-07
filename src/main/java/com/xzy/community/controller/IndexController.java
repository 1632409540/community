package com.xzy.community.controller;

import com.xzy.community.dto.PaginationDTO;
import com.xzy.community.mapper.UserMapper;
import com.xzy.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String greeting(@RequestParam(name="name", required=false,
            defaultValue="World") String name, Model model,
                           HttpServletRequest request,
                           @RequestParam(name = "page",defaultValue = "1")Integer page,
                           @RequestParam(name = "size",defaultValue = "7")Integer size
                           ) {
        model.addAttribute("name", name);


        PaginationDTO paginationDTO=questionService.list(page,size);

        model.addAttribute("paginationDTO",paginationDTO);


        return "index";
    }
}
