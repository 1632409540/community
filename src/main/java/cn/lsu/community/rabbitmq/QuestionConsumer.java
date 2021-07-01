package cn.lsu.community.rabbitmq;


import cn.lsu.community.elasticsearch.QuestionRespository;
import cn.lsu.community.entity.Question;
import cn.lsu.community.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

//问题消费者
@Component
@Slf4j
public class QuestionConsumer {

    @Autowired
    protected QuestionRespository questionRespository;
    @Resource
    private QuestionService questionService;
    //保存/更新文档到ES
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "es",type = "topic"),
                    key = {"question.save"}
            )
    })
    public void saveQuestion(String message){
        log.warn("【RabbitMq】保存文章的消息，ID："+message);
        Question question = questionService.queryById(Long.parseLong(message));
        questionRespository.save(question);
        log.warn("【RabbitMq】消费成功");
    }

    //从ES中删除文档
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "es",type = "topic"),
                    key = {"question.delete"}
            )
    })
    public void deleteQuestion(String message){
        log.warn("【RabbitMq】删除文章的消息，ID："+message);
        Question question = new Question();
        question.setId(Long.parseLong(message));
        questionRespository.delete(question);
        log.warn("【RabbitMq】消费成功");
    }
}
