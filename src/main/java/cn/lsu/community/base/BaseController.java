package cn.lsu.community.base;

import cn.lsu.community.entity.Question;
import cn.lsu.community.entity.SystemSet;
import cn.lsu.community.entity.User;
import cn.lsu.community.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class BaseController {

    @Resource
    protected UserService userService;

    @Resource
    protected QuestionService questionService;

    @Resource
    protected CommentService commentService;

    @Resource
    protected TagService tagService;

    @Resource
    protected NotificationService notificationService;

    @Resource
    protected SystemSetService systemSetService;

    protected User loginUser;
    protected User admin;

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected HttpSession session;

    public User getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
    }

    @ModelAttribute
    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.session = request.getSession(true);
        // 设置登录的用户
        loginUser = (User)session.getAttribute("user");
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    @ModelAttribute
    public void setAdminReqAndRes(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.session = request.getSession(true);
        // 设置登录的用户
        admin = (User)session.getAttribute("admin");
    }

}
