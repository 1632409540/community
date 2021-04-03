package cn.lsu.community.service.Impl;

import cn.lsu.community.base.BaseService;
import cn.lsu.community.entity.UserLike;
import cn.lsu.community.mapper.UserLikeMapper;
import cn.lsu.community.mapper.UserMapper;
import cn.lsu.community.entity.User;
import cn.lsu.community.service.UserService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl extends BaseService<UserMapper,User> implements UserService {

    @Resource
    private UserLikeMapper userLikeMapper;

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

    @Override
    public void changeLikeUser(Long loginUserId, Long likedUserId) {
        UserLike userLike = new UserLike();
        userLike.setUserId(loginUserId);
        userLike.setLikedUserId(likedUserId);
        UserLike userLike1 = userLikeMapper.selectOne(userLike);
        if(ObjectUtils.isNotEmpty(userLike1)){
            userLikeMapper.deleteById(userLike1.getId());
        }else {
            userLike.setCreateDate(new Date());
            userLikeMapper.insert(userLike);
        }
    }

    @Override
    public User queryByNameOrEail(String name) {
        Wrapper<User> wrapper = new EntityWrapper<>();
        wrapper.eq("name",name)
                .or().eq("email",name);
        List<User> users = baseMapper.selectList(wrapper);
        if(ObjectUtils.isNotEmpty(users)&&users.size()>0){
            return users.get(0);
        }else {
            return null;
        }
    }
}
