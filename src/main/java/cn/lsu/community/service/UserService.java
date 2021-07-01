package cn.lsu.community.service;

import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.dto.QuestionDTO;
import cn.lsu.community.entity.User;

public interface UserService {
    public void createOrUpdate(User user);

    void changeLikeUser(Long loginUserId, Long likedUserId);

    User queryByNameOrEail(String name);

    PaginationDTO list(User loginUser, String search, Integer page, Integer size);

    boolean updateById(User User);

    void updateUserIntegral(User addUser,User delUser,Integer integral);

    User findById(Long id);

    void updateAvatarById(Long id, String imgPath);

    boolean insertUser(User user);

    boolean judgeNameExist(String name);

    PaginationDTO selectAll();
}
