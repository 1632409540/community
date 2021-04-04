package cn.lsu.community.mapper;

import cn.lsu.community.base.BaseMapper;
import cn.lsu.community.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    Integer selectCountBySearch(@Param("search") String search);

    List<User> selectPageBySearch(@Param("search") String search,
                                  @Param("offSize") Integer offSize, @Param("size") Integer size);
}