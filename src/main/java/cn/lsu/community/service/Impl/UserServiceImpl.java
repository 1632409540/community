package cn.lsu.community.service.Impl;

import cn.lsu.community.base.BaseService;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.entity.Comment;
import cn.lsu.community.entity.Tag;
import cn.lsu.community.entity.UserLike;
import cn.lsu.community.enums.NotificationType;
import cn.lsu.community.exception.CustomizeErrorCode;
import cn.lsu.community.exception.CustomizeException;
import cn.lsu.community.mapper.TagMapper;
import cn.lsu.community.mapper.UserLikeMapper;
import cn.lsu.community.mapper.UserMapper;
import cn.lsu.community.entity.User;
import cn.lsu.community.service.NotificationService;
import cn.lsu.community.service.UserService;
import cn.lsu.community.utils.MailUtils;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends BaseService<UserMapper,User> implements UserService {

    @Resource
    private UserLikeMapper userLikeMapper;

    @Resource
    private TagMapper tagMapper;

    @Resource
    private NotificationService notificationService;
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
            User delUser = baseMapper.selectById(loginUserId);
            User addUser = baseMapper.selectById(likedUserId);
            this.updateUserIntegral(addUser,delUser,10);
            if (addUser.getNotifiLike() == 1) {
                Comment comment = new Comment();
                comment.setCommentator(delUser.getId());
                notificationService.createCommentNotify(comment, NotificationType.LIKE_YOU.getType(), addUser.getId(), delUser.getName(), null, "");
            }
            if (addUser.getEmailLike() == 1) {
                try {
                    //邮件发送正文
                    String context = delUser.getName() + NotificationType.LIKE_YOU.getName();
                    //发送邮件
                    MailUtils.sendMail(addUser.getEmail(), context, "校园博客系统-通知");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
            if(ObjectUtils.isNotEmpty(loginUser)){
                Wrapper<UserLike> wrapper = new EntityWrapper<>();
                wrapper.eq("liked_user_id",user.getId());
                Integer likeCount = userLikeMapper.selectCount(wrapper);
                user.setLikeCount(likeCount);
                wrapper.andNew().eq("user_id",loginUser.getId());
                Integer count = userLikeMapper.selectCount(wrapper);
                if(count>0){
                    user.setMyLike(true);
                }else {
                    user.setMyLike(false);
                }
            }
            List<Tag> goodTags = tagMapper.selectGoodTags(user.getId());
            user.setGoodTags(goodTags);
        });
        PaginationDTO<User> paginationDTO=new PaginationDTO<User>();
        paginationDTO.setData(users);
        paginationDTO.setPagination(totalCount,page,size);
        return paginationDTO;
    }

    @Override
    public boolean updateById(User entity) {
        entity.setLastModified(new Date());
        return super.updateById(entity);
    }

    @Override
    public void updateUserIntegral(User addUser,User delUser,Integer integral) {
//        if(addUser.equals(delUser)){
//            return;
//        }
        if(ObjectUtils.isNotEmpty(delUser)){
            User del = new User();
            del.setId(delUser.getId());
            if(delUser.getIntegral()-integral<0){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            del.setIntegral(delUser.getIntegral()-integral);
            baseMapper.updateById(del);
        }
        if(ObjectUtils.isNotEmpty(addUser)){
            User add = new User();
            add.setId(addUser.getId());
            add.setIntegral(addUser.getIntegral()+integral);
            baseMapper.updateById(add);
        }
    }

    @Override
    public User findById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public void updateAvatarById(Long id, String imgPath) {
        User user = new User();
        user.setId(id);
        user.setAvatarUrl(imgPath);
        baseMapper.updateById(user);
    }

    @Override
    public boolean insertUser(User user) {
        user.setCreateDate(new Date());
        user.setLastModified(new Date());
        user.setIntegral(19);
        Integer insert = baseMapper.insert(user);
        if(insert>0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean judgeNameExist(String name) {
        User userWrapper = new User();
        userWrapper.setName(name);
        User dbUser = baseMapper.selectOne(userWrapper);
        if(dbUser!=null){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public PaginationDTO selectAll() {
        Wrapper<User> wrapper = new EntityWrapper<>();
        wrapper.orderBy("status",false);
        Integer totalCount= baseMapper.selectCount(wrapper);
        List<User> users= baseMapper.selectList(wrapper);
        PaginationDTO<User> paginationDTO=new PaginationDTO<User>();
        paginationDTO.setData(users);
        paginationDTO.setPagination(totalCount,1,1);
        return paginationDTO;
    }
}
