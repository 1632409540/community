package cn.lsu.community.task;

import cn.lsu.community.base.BaseService;
import cn.lsu.community.entity.Question;
import cn.lsu.community.mapper.QuestionMapper;
import cn.lsu.community.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

//关于文章的定时器任务
@Service
@Slf4j
public class QuestionTask extends BaseService<QuestionMapper,Question> {

    @Resource
    private QuestionService questionService;
    /**
     * 定时任务将数据保存到ES：每天凌晨0：00
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void saveToEs(){
        log.warn("【定时任务】开始保存数据到ES");
        long startTime = System.currentTimeMillis();
        // 先清空再添加
        questionService.initEs();
        long endTime = System.currentTimeMillis();
        log.warn("【定时任务】结束保存数据到ES，"+"程序运行时间：" + (endTime - startTime) + "ms");
    }
}
