package cn.lsu.community.controller;

import cn.hutool.crypto.digest.MD5;
import cn.lsu.community.base.BaseController;
import cn.lsu.community.dto.GithubUser;
import cn.lsu.community.dto.AccessTokenDTO;
import cn.lsu.community.entity.User;
import cn.lsu.community.provider.GithubProvider;
import cn.lsu.community.redis.RedisKey;
import cn.lsu.community.service.Impl.NotificationServiceImpl;
import cn.lsu.community.service.Impl.UserServiceImpl;
import cn.lsu.community.service.NotificationService;
import cn.lsu.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
@Slf4j
public class AuthorizeController extends BaseController {
    @Autowired
    private GithubProvider githubProvider;
    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code")String code,
                           @RequestParam(name = "state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setRedirect_uri(redirectUri);
        String accessTocken = githubProvider.getAccessTocken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessTocken);
        if(githubUser!=null&&githubUser.getId()!=null){
            User user=new User();
            String token =UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setBio(githubUser.getBio());
            user.setAvatarUrl(githubUser.getAvatarUrl());
            UUID uuid = UUID.randomUUID();
            String substring = uuid.toString().substring(0, 8);
            user.setSalt(substring);
            // 获取Md5加密对象
            MD5 md5 = new MD5(user.getSalt().getBytes());
            user.setPassword(md5.digestHex16("123456"));
            userService.createOrUpdate(user);
            //登录成功
            response.addCookie(new Cookie("token",token));
            RedisKey redisKey = new RedisKey(user.getToken());
            redisService.incr(redisKey,user.getToken());
            redisService.expice(redisKey,user.getToken(),60*10);
            return "redirect:/";
        }else{
            //登录失败
            log.error("GithubUser login error",githubUser);
            return "redirect:/";
        }
    }
}
