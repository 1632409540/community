package cn.lsu.community.controller;

import cn.hutool.crypto.digest.MD5;
import cn.lsu.community.base.BaseController;
import cn.lsu.community.dto.HotTopicDTO;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.dto.ResultDTO;
import cn.lsu.community.entity.User;
import cn.lsu.community.redis.RedisKey;
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
public class LoginController extends BaseController {

    @GetMapping("/toLogin")
    public String index() {
        return "login";
    }

    @GetMapping("/toAdminLogin")
    public String adminIndex() {
        return "admin/login";
    }

    /**
     * 普通用户用户名、邮箱登录
     */
    @PostMapping("/login")
    @ResponseBody
    public ResultDTO login(@RequestParam(name = "name") String name,@RequestParam(name = "password") String password,
                                    HttpServletResponse response,HttpServletRequest request){
        User user = userService.queryByNameOrEail(name);
        if(ObjectUtils.isEmpty(user)){
            return ResultDTO.errorOf(100,"你输入的用户名/邮箱不存在！");
        }
        if(user.getStatus() == 0){
            return ResultDTO.errorOf(400,"你的账号已被管理员冻结!");
        }
        // 获取Md5加密对象
        MD5 md5 = new MD5(user.getSalt().getBytes());
        // 进行判断
        if (user.getPassword().equals(md5.digestHex16(password))){
            //登录成功
            response.addCookie(new Cookie("token",user.getToken()));
            request.getSession().setAttribute("admin",user);
            RedisKey redisKey = new RedisKey(user.getToken());
            redisService.incr(redisKey,user.getToken());
            redisService.expice(redisKey,user.getToken(),60*10);
            return ResultDTO.successOf();
        }else {
            return ResultDTO.errorOf(100,"你输入的密码错误!");
        }
    }

    /**
     * 普通用户用户名、邮箱登录
     */
    @PostMapping("/admin/login")
    @ResponseBody
    public ResultDTO adminLogin(@RequestParam(name = "name") String name,@RequestParam(name = "password") String password,
                           HttpServletResponse response,HttpServletRequest request){
        User user = userService.queryByNameOrEail(name);
        if(ObjectUtils.isEmpty(user)){
            return ResultDTO.errorOf(100,"你输入的用户名/邮箱不存在！");
        }
        if(user.getStatus() != 2){
            return ResultDTO.errorOf(400,"你不是管理员，不能登录后台管理!");
        }
        // 获取Md5加密对象
        MD5 md5 = new MD5(user.getSalt().getBytes());
        // 进行判断
        if (user.getPassword().equals(md5.digestHex16(password))){
            //登录成功
            request.getSession().setAttribute("admin",user);
            response.addCookie(new Cookie("adminToken",user.getToken()));
            RedisKey redisKey = new RedisKey(user.getToken());
            redisService.incr(redisKey,user.getToken());
            redisService.expice(redisKey,user.getToken(),60*10);
            return ResultDTO.successOf();
        }else {
            return ResultDTO.errorOf(100,"你输入的密码错误!");
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response){
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

    @GetMapping("/admin/logout")
    public String adminnLogout(HttpServletRequest request,
                         HttpServletResponse response){
        request.getSession().removeAttribute("admin");
        Cookie cookie = new Cookie("adminToken", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/toAdminLogin";
    }

}
