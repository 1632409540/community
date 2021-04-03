package cn.lsu.community.controller;

import cn.hutool.crypto.digest.MD5;
import cn.lsu.community.dto.HotTopicDTO;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.dto.ResultDTO;
import cn.lsu.community.entity.User;
import cn.lsu.community.service.QuestionService;
import cn.lsu.community.service.TagService;
import cn.lsu.community.service.UserService;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private QuestionService questionService;
    @Resource
    private TagService tagService;
    @Resource
    private UserService userService;

    @GetMapping("/toLogin")
    public String index() {
        return "login";
    }

    /**
     * 用户名、邮箱登录
     */
    @PostMapping("/login")
    @ResponseBody
    public ResultDTO login(@RequestParam(name = "name") String name,@RequestParam(name = "password") String password,
                                    HttpServletResponse response,HttpServletRequest request){
        User user = userService.queryByNameOrEail(name);
        if(ObjectUtils.isEmpty(user)){
            return ResultDTO.errorOf(100,"用户名/邮箱不存在！");
        }
        // 获取Md5加密对象
        MD5 md5 = new MD5(user.getSalt().getBytes());
        // 进行判断
        if (user.getPassword().equals(md5.digestHex16(password))){
            //登录成功
            request.getSession().setAttribute("user",user);
            response.addCookie(new Cookie("token",user.getToken()));
            return ResultDTO.successOf();
        }else {
            return ResultDTO.errorOf(100,"密码错误!");
        }
    }
}
