package cn.lsu.community.controller;

import cn.lsu.community.entity.Notification;
import cn.lsu.community.entity.User;
import cn.lsu.community.service.CommentService;
import cn.lsu.community.service.Impl.CommentServiceImpl;
import cn.lsu.community.service.Impl.NotificationServiceImpl;
import cn.lsu.community.service.Impl.QuestionServiceImpl;
import cn.lsu.community.service.NotificationService;
import cn.lsu.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

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
