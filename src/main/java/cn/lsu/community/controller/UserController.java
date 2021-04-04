package cn.lsu.community.controller;

import cn.hutool.crypto.digest.MD5;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.entity.QuestionLike;
import cn.lsu.community.entity.User;
import cn.lsu.community.service.NotificationService;
import cn.lsu.community.service.QuestionService;
import cn.lsu.community.service.TagService;
import cn.lsu.community.service.UserService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
public class UserController {
    @Resource
    private QuestionService questionService;
    @Resource
    private UserService userService;

    @GetMapping("/userSetting/{section}")
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
        if("userMessage".contains(section)){
            model.addAttribute("section","userMessage");
            model.addAttribute("sectionName","基本资料");
            PaginationDTO paginationDTO=questionService.list(user.getId(),page,size,1);
            model.addAttribute("paginationDTO",paginationDTO);
        }
        return "userSetting";
    }

    @GetMapping("/users")
    public String index(@RequestParam(name ="search",required = false) String search,
                        @RequestParam(name = "page",defaultValue = "1",required = false)Integer page,
                        @RequestParam(name = "size",defaultValue = "6",required = false)Integer size,
                        HttpServletRequest request,
                        Model model) {

        User user= (User) request.getSession().getAttribute("user");
        PaginationDTO paginationDTO=userService.list(user,search,page,size);
        model.addAttribute("search", search);
        model.addAttribute("paginationDTO",paginationDTO);
        request.getSession().setAttribute("navbarStatus","users");
        return "users";
    }

    @GetMapping("/likeUser")
    public String likeQuestion(@RequestParam(name ="search",required = false) String search,
                               @RequestParam(name = "page",defaultValue = "1",required = false)Integer page,
                               @RequestParam(name = "size",defaultValue = "6",required = false)Integer size,
                               @RequestParam(name ="userId") Long userId,
                               HttpServletRequest request){
        User user= (User) request.getSession().getAttribute("user");
        userService.changeLikeUser(user.getId(),userId);
        return "redirect:/users?search="+search+"&page="+page;
    }

}
