package com.xzy.community.controller;

import com.xzy.community.dto.PaginationDTO;
import com.xzy.community.mapper.UserMapper;
import com.xzy.community.model.Notification;
import com.xzy.community.model.User;
import com.xzy.community.service.NotificationService;
import com.xzy.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{section}")
    public String profile(@PathVariable(name = "section")String section,
                          Model model,
                          HttpServletRequest request,
                          @RequestParam(name = "page",defaultValue = "1")Integer page,
                          @RequestParam(name = "size",defaultValue = "7")Integer size){

        User user= (User) request.getSession().getAttribute("user");
        if(user==null){
            return "redirect:/";
        }
        if("questions".contains(section)){
            model.addAttribute("section","questions");
            model.addAttribute("sectionName","我的提问");
            PaginationDTO paginationDTO=questionService.list(user.getId(),page,size);
            model.addAttribute("paginationDTO",paginationDTO);
        }
        if("replies".contains(section)){
            model.addAttribute("section","replies");
            model.addAttribute("sectionName","最新回复");
            PaginationDTO paginationDTO=notificationService.list(user,page,size);
            model.addAttribute("paginationDTO",paginationDTO);
            Long unreadCount=notificationService.getUnReadCount(user.getId());
            request.getSession().setAttribute("unreadCount",unreadCount);
        }
        return "profile";
    }
}
