package cn.lsu.community.controller;

import cn.lsu.community.base.BaseController;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.entity.User;
import cn.lsu.community.service.Impl.NotificationServiceImpl;
import cn.lsu.community.service.Impl.QuestionServiceImpl;
import cn.lsu.community.service.NotificationService;
import cn.lsu.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController extends BaseController {


    @GetMapping("/profile/{section}")
    public String profile(@PathVariable(name = "section")String section,
                          Model model,
                          HttpServletRequest request,
                          @RequestParam(name = "page",defaultValue = "1")Integer page,
                          @RequestParam(name = "size",defaultValue = "10")Integer size){
        request.getSession().setAttribute("navbarStatus","");
        User user= (User) request.getSession().getAttribute("user");
        if(user==null){
            return "redirect:/";
        }
        if("questions".contains(section)){
            model.addAttribute("section","questions");
            model.addAttribute("sectionName","发布的提问");
            PaginationDTO paginationDTO=questionService.list(user.getId(),page,size,1);
            model.addAttribute("paginationDTO",paginationDTO);
        }
        if("draft".contains(section)){
            model.addAttribute("section","draft");
            model.addAttribute("sectionName","草稿箱");
            PaginationDTO paginationDTO=questionService.list(user.getId(),page,size,0);
            model.addAttribute("paginationDTO",paginationDTO);
        }
        if("deleteLight".contains(section)){
            model.addAttribute("section","deleteLight");
            model.addAttribute("sectionName","回收站");
            PaginationDTO paginationDTO=questionService.list(user.getId(),page,size,2);
            model.addAttribute("paginationDTO",paginationDTO);
        }
        if("replies".contains(section)){
            request.getSession().setAttribute("navbarStatus","replies");
            model.addAttribute("section","replies");
            model.addAttribute("sectionName","最新通知");
            PaginationDTO paginationDTO=notificationService.list(user,page,size);
            model.addAttribute("paginationDTO",paginationDTO);
            Long unreadCount=notificationService.getUnReadCount(user.getId());
            request.getSession().setAttribute("unreadCount",unreadCount);
        }
        return "profile";
    }
}
