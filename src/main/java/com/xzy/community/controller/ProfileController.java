package com.xzy.community.controller;

import com.xzy.community.dto.PaginationDTO;
import com.xzy.community.mapper.UserMapper;
import com.xzy.community.model.User;
import com.xzy.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionService questionService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action")String action,
                          Model model,
                          HttpServletRequest request,
                          @RequestParam(name = "page",defaultValue = "1")Integer page,
                          @RequestParam(name = "size",defaultValue = "7")Integer size){
        User user=null;
        Cookie[] cookies = request.getCookies();
        if(cookies!=null&&cookies.length>0){
            for(Cookie cookie: cookies){
                if(cookie.getName().equals("token")){
                    String token=cookie.getValue();
                    user =userMapper.selectByToken(token);
                }
            }
        }
        if(user==null){
            return "redirect:/";
        }
        if("questions".contains(action)){
            model.addAttribute("section","questions");
            model.addAttribute("sectionName","我的提问");
        }
        if("replies".contains(action)){
            model.addAttribute("section","replies");
            model.addAttribute("sectionName","我的回复");
        }

        PaginationDTO paginationDTO=questionService.list(user.getId(),page,size);
        model.addAttribute("paginationDTO",paginationDTO);
        return "profile";
    }
}
