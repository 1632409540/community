package cn.lsu.community.service;

import cn.lsu.community.dto.HotTopicDTO;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.dto.QuestionDTO;
import cn.lsu.community.entity.Question;
import cn.lsu.community.entity.Tag;

import java.util.List;

public interface QuestionService {
    public PaginationDTO list(Long tagId,String search, Integer page, Integer size);

    public PaginationDTO list(Long userId, Integer page, Integer size,Integer status);

    public QuestionDTO findById(Long id);

    public Question createOrUpdate(QuestionDTO question);

    public void addViewCount(Long id);

    public List<HotTopicDTO> getHotTopic();

    void deleteById(Long id);

    List<Question> listLikeQuestions(Long id, List<Tag> tags);

    void addLikeCount(Long questionId);

    void delLikeCount(Long questionId);

    boolean checkMyLike(Long userId, Long questionId);
}
