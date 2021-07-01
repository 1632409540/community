package cn.lsu.community.elasticsearch;

import cn.lsu.community.dto.QuestionDTO;
import cn.lsu.community.entity.Question;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface QuestionRespository extends ElasticsearchRepository<Question,Integer> {
}
