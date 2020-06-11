package com.xzy.community.controller;

import com.xzy.community.dto.CommentDTO;
import com.xzy.community.dto.QuestionDTO;
import com.xzy.community.dto.ResultDTO;
import com.xzy.community.enums.CommentTypeEnum;
import com.xzy.community.exception.CustomizeErrorCode;
import com.xzy.community.model.Notification;
import com.xzy.community.model.Question;
import com.xzy.community.model.User;
import com.xzy.community.service.CommentService;
import com.xzy.community.service.NotificationService;
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
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/notification/{id}")
    public String question(@PathVariable(name = "id")Long id, HttpServletRequest request){

        User user= (User) request.getSession().getAttribute("user");
        if(user==null){
            return "redirect:/";
        }
        Notification notification=notificationService.viewNotificationById(user,id);
        if(notification==null){
            return "redirect:/";
        }
        return "redirect:/question/"+notification.getOuterId();
    }
}
