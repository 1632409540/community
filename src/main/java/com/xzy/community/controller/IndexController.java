package com.xzy.community.controller;

import com.xzy.community.mapper.UserMapper;
import com.xzy.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/")
    public String greeting(@RequestParam(name="name", required=false,
            defaultValue="World") String name, Model model, HttpServletRequest request) {
        model.addAttribute("name", name);
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie: cookies){
            if(cookie.getName().equals("token")){
                String token=cookie.getValue();
                User user =userMapper.selectByToken(token);
                if(user!=null){
                    request.getSession().setAttribute("user",user);
                    break;
                }
            }
        }

        return "index";
    }
}
