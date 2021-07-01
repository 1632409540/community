package cn.lsu.community.interceptor;


import cn.lsu.community.entity.SystemSet;
import cn.lsu.community.mapper.UserMapper;
import cn.lsu.community.entity.User;
import cn.lsu.community.redis.RedisKey;
import cn.lsu.community.redis.RedisService;
import cn.lsu.community.service.Impl.NotificationServiceImpl;
import cn.lsu.community.service.SystemSetService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class SessionInterceptor implements HandlerInterceptor {
    @Resource
    private UserMapper userMapper;
    @Resource
    private NotificationServiceImpl notificationService;
    @Resource
    private SystemSetService systemSetService;
    @Resource
    private RedisService redisService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Cookie[] cookies = request.getCookies();
        if(cookies!=null&&cookies.length>0){
            for(Cookie cookie: cookies){
                if(cookie.getName().equals("token")&& ObjectUtils.isNotEmpty(cookie.getValue())){
                    String token=cookie.getValue();
                    RedisKey redisKey = new RedisKey(token);
                    if(redisService.exists(redisKey,token)){
                        redisService.expice(redisKey,token,60*10);
                    }else {
                        request.getSession().setAttribute("user",null);
                        break;
                    }

                    User userEntity = new User();
                    userEntity.setToken(token);
                    User user = userMapper.selectOne(userEntity);
                    if(ObjectUtils.isNotEmpty(user)){
                        request.getSession().setAttribute("user",user);
                        Long unreadCount=notificationService.getUnReadCount(user.getId());
                        request.getSession().setAttribute("unreadCount",unreadCount);
                        break;
                    }
                }
                if(cookie.getName().equals("adminToken")&& ObjectUtils.isNotEmpty(cookie.getValue())){
                    String token=cookie.getValue();
                    RedisKey redisKey = new RedisKey(token);
                    if(redisService.exists(redisKey,token)){
                        redisService.expice(redisKey,token,60*10);
                    }else {
                        request.getSession().setAttribute("admin",null);
                        break;
                    }
                    User userEntity = new User();
                    userEntity.setToken(token);
                    User user = userMapper.selectOne(userEntity);
                    if(ObjectUtils.isNotEmpty(user)){
                        request.getSession().setAttribute("admin",user);
                        break;
                    }
                }
            }
        }
        SystemSet systemSet = (SystemSet) request.getSession().getAttribute("systemSet");
        if(ObjectUtils.isEmpty(systemSet)){
            request.getSession().setAttribute("systemSet",systemSetService.findSystemSet());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
