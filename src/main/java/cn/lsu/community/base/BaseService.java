package cn.lsu.community.base;

import cn.lsu.community.elasticsearch.QuestionRespository;
import cn.lsu.community.mapper.*;
import cn.lsu.community.service.*;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

public class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    protected RestHighLevelClient client;
    @Autowired
    protected QuestionRespository questionRespository;

}
