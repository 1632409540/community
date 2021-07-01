package cn.lsu.community.service.Impl;

import cn.lsu.community.base.BaseService;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.entity.SystemSet;
import cn.lsu.community.entity.Tag;
import cn.lsu.community.entity.User;
import cn.lsu.community.entity.UserLike;
import cn.lsu.community.exception.CustomizeErrorCode;
import cn.lsu.community.exception.CustomizeException;
import cn.lsu.community.mapper.SystemSetMapper;
import cn.lsu.community.mapper.TagMapper;
import cn.lsu.community.mapper.UserLikeMapper;
import cn.lsu.community.mapper.UserMapper;
import cn.lsu.community.service.SystemSetService;
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
public class SystemSetServiceImpl extends BaseService<SystemSetMapper, SystemSet> implements SystemSetService {

    @Override
    public SystemSet findSystemSet() {
        return baseMapper.selectOne(new SystemSet());
    }

    @Override
    public void updateBySystemSetId(SystemSet systemSet) {
        baseMapper.updateById(systemSet);
    }
}
