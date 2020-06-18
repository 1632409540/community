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
    private QuestionService questionService;

    @GetMapping("/")
    public String index(@RequestParam(name="name", required=false, defaultValue="World") String name,
                           Model model,
                           @RequestParam(name = "page",defaultValue = "1")Integer page,
                           @RequestParam(name = "size",defaultValue = "10")Integer size,
                           @RequestParam(name ="search",required = false) String search,
                            @RequestParam(name ="tag",required = false) String tag
                           ) {
        PaginationDTO paginationDTO=questionService.list(null,search,tag,page,size);
        model.addAttribute("name", name);
        model.addAttribute("search", search);
        model.addAttribute("tag", tag);
        model.addAttribute("paginationDTO",paginationDTO);
        return "index";
    }
}
