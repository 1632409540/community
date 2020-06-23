package com.xzy.community.service;

import com.xzy.community.dto.HotTopicDTO;
import com.xzy.community.dto.PaginationDTO;
import com.xzy.community.dto.QuestionDTO;
import com.xzy.community.dto.QuestionQueryDTO;
import com.xzy.community.exception.CustomizeErrorCode;
import com.xzy.community.exception.CustomizeException;
import com.xzy.community.mapper.QuestionExtMapper;
import com.xzy.community.mapper.QuestionMapper;
import com.xzy.community.mapper.UserMapper;
import com.xzy.community.model.Question;
import com.xzy.community.model.QuestionExample;
import com.xzy.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;

    public PaginationDTO list(Long id, String search,String tag,Integer page, Integer size) {

        Integer offSize=size*(page-1);
        if(StringUtils.isNotBlank(search)){
            String[] searchs=search.split(" ");
            search= Arrays.stream(searchs).collect(Collectors.joining("|"));
        }
        if(StringUtils.isNotBlank(tag)){
            String[] tags=tag.split(",");
            tag= Arrays.stream(tags).collect(Collectors.joining("|"));
        }
        QuestionQueryDTO questionQueryDTO=new QuestionQueryDTO();
        questionQueryDTO.setId(id);
        questionQueryDTO.setSearch(search);
        questionQueryDTO.setTag(tag);
        questionQueryDTO.setPage(offSize);
        questionQueryDTO.setSize(size);
        Integer totalCount= questionExtMapper.countBySearchAndTagExceptId(questionQueryDTO);


        QuestionExample example=new QuestionExample();
        example.setOrderByClause("gmt_create desc");
        List<Question> questionList= questionExtMapper.selectBySearchAndTagExceptId(questionQueryDTO);

        List<QuestionDTO> questionDTOList=questionList.stream().map(question -> {
            User user=userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO=new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            return  questionDTO;
        }).collect(Collectors.toList());

        PaginationDTO<QuestionDTO> paginationDTO=new PaginationDTO<QuestionDTO>();
        paginationDTO.setData(questionDTOList);
        paginationDTO.setPagination(totalCount,page,size);
        return paginationDTO;
    }

    public PaginationDTO list(Long id, Integer page, Integer size) {
        Integer offSize=size*(page-1);
        QuestionExample example=new QuestionExample();
        example.createCriteria().andCreatorEqualTo(id);
        example.setOrderByClause("gmt_create desc");
        List<Question> questionList=questionMapper.selectByExampleWithBLOBsWithRowbounds(example,new RowBounds(offSize,size));
        List<QuestionDTO> questionDTOList=new LinkedList<>();
        PaginationDTO<QuestionDTO> paginationDTO=new PaginationDTO<QuestionDTO>();
        for(Question question:questionList){
            User user=userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO=new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        QuestionExample example2=new QuestionExample();
        example.createCriteria().andIdEqualTo(id);
        Integer totalCount=(int)questionMapper.countByExample(example);
        paginationDTO.setPagination(totalCount,page,size);
        return paginationDTO;
    }

    public QuestionDTO findById(Long id) {
        Question question=questionMapper.selectByPrimaryKey(id);
        if(question==null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO=new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user=userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        Question dbQuestion= questionMapper.selectByPrimaryKey(question.getId());
        if(dbQuestion!=null){
            Question updateQuestion=new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());

            QuestionExample example=new QuestionExample();
            example.createCriteria().andIdEqualTo(dbQuestion.getId());
            int update=questionMapper.updateByExampleSelective(updateQuestion,example);
            if(update!=1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }else {
            question.setId(null);
            try{
                questionMapper.insertSelective(question);
            }catch (Exception e){
                e.printStackTrace();
                throw new CustomizeException(CustomizeErrorCode.QUESTION_INSERT_ERROR);
            }
        }
    }
    public void addViewCount(Long id) {
        Question question=new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.addViewCount(question);
    }

    public HotTopicDTO getHotTopic() {
        HotTopicDTO hotTopicDTO=new HotTopicDTO();
        QuestionExample example=new QuestionExample();
        List<Question> questions = questionMapper.selectByExample(example);
        HashMap<String,Integer> map=new HashMap<String,Integer>();
        for (Question question : questions) {
            String[] split = StringUtils.split(question.getTag(), ",");
            for (String tag:split) {
                if(map.containsKey(tag)){
                    map.put(tag,map.get(tag)+1);
                }else {
                    map.put(tag,1);
                }
            }
        }
        Map<String, Integer> sortedMap = this.sortByValue(map, true);

        LinkedList<HotTopicDTO> data =new LinkedList<>();
        Iterator<Map.Entry< String,Integer>> it = sortedMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String,Integer> entry = it.next();
            HotTopicDTO hotTopicDTO1=new HotTopicDTO();
            hotTopicDTO1.setTag(entry.getKey());
            hotTopicDTO1.setQuestionCount(entry.getValue());
            int count =questionExtMapper.getCommentCountByTag("%"+entry.getKey()+"%");
            hotTopicDTO1.setCommentCount(count);
            data.add(hotTopicDTO1);
        }
        hotTopicDTO.setData(data);
        return hotTopicDTO;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean isDesc) {
        Map<K, V> result = new LinkedHashMap<>();
        if (isDesc) {
            map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByValue().reversed())
                    .forEach(e -> result.put(e.getKey(), e.getValue()));
        } else {
            map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByValue())
                    .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        }
        return result;
    }
}
