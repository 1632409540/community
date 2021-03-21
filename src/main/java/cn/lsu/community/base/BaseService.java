package cn.lsu.community.base;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

}
