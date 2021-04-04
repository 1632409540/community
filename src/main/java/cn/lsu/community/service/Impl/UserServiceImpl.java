package cn.lsu.community.service.Impl;

import cn.lsu.community.base.BaseService;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.entity.UserLike;
import cn.lsu.community.mapper.UserLikeMapper;
import cn.lsu.community.mapper.UserMapper;
import cn.lsu.community.entity.User;
import cn.lsu.community.service.UserService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public PaginationDTO list(User loginUser, String search, Integer page, Integer size) {
        Integer offSize=size*(page-1);
        if(StringUtils.isNotEmpty(search)){
            String[] searchs=search.split(" ");
            search= Arrays.stream(searchs).collect(Collectors.joining("|"));
        }
        Integer totalCount= baseMapper.selectCountBySearch(search);
        List<User> users= baseMapper.selectPageBySearch(search,offSize,size);
        users.stream().forEach(user -> {
            Wrapper<UserLike> wrapper = new EntityWrapper<>();
            wrapper.eq("user_id",loginUser.getId())
                    .eq("liked_user_id",user.getId());
            Integer count = userLikeMapper.selectCount(wrapper);
            if(count>0){
                user.setMyLike(true);
            }else {
                user.setMyLike(false);
            }
        });
        PaginationDTO<User> paginationDTO=new PaginationDTO<User>();
        paginationDTO.setData(users);
        paginationDTO.setPagination(totalCount,page,size);
        return paginationDTO;
    }
}
