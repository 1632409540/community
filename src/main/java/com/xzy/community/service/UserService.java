package com.xzy.community.service;

import com.xzy.community.mapper.UserMapper;
import com.xzy.community.model.User;
import com.xzy.community.model.UserExample;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(User user) {
        UserExample userExample=new UserExample();
        userExample.createCriteria().andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);

        User dbUser= null;
        if(users.size()!=0){
            dbUser=users.get(0);
        }
       if(dbUser!=null){
           User updateUser=new User();
           updateUser.setToken(user.getToken());
           updateUser.setName(user.getName());
           updateUser.setBio(user.getBio());
           updateUser.setGmtModified(user.getGmtCreate());
           updateUser.setAvatarUrl(user.getAvatarUrl());
           UserExample userExample2=new UserExample();
           userExample.createCriteria().andIdEqualTo(dbUser.getId());
           userMapper.updateByExampleSelective(updateUser,userExample2);
       }else {
           userMapper.insert(user);
       }
    }
}
