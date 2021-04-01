package cn.lsu.community.service.Impl;

import cn.lsu.community.base.BaseService;
import cn.lsu.community.mapper.UserMapper;
import cn.lsu.community.entity.User;
import cn.lsu.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl extends BaseService<UserMapper,User> implements UserService {

    @Override
    public void createOrUpdate(User user) {
       User userWrapper = new User();
       userWrapper.setAccountId(user.getAccountId());
       User dbUser = baseMapper.selectOne(userWrapper);
       if(dbUser!=null){
           user.setId(dbUser.getId());
           baseMapper.updateById(user);
       }else {
           user.setCreateDate(new Date());
           baseMapper.insert(user);
       }
    }
}
