package cn.lsu.community.service;

import cn.lsu.community.dto.HotTopicDTO;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.dto.QuestionDTO;
import cn.lsu.community.entity.Question;

public interface QuestionService {
    public PaginationDTO list(Long id, String search, String tag, Integer page, Integer size);

    public PaginationDTO list(Long userId, Integer page, Integer size,Integer status);

    public QuestionDTO findById(Long id);

    public Question createOrUpdate(Question question);

    public void addViewCount(Long id);

    public HotTopicDTO getHotTopic();

    void deleteById(Long id);
}
