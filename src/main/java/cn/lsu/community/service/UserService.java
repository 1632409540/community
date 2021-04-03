package cn.lsu.community.service;

import cn.lsu.community.entity.User;

public interface UserService {
    public void createOrUpdate(User user);

    void changeLikeUser(Long loginUserId, Long likedUserId);

    User queryByNameOrEail(String name);
}
