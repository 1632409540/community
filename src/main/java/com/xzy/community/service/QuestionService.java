package com.xzy.community.service;

import com.xzy.community.dto.PaginationDTO;
import com.xzy.community.dto.QuestionDTO;
import com.xzy.community.dto.QuestionQueryDTO;
import com.xzy.community.exception.CustomizeErrorCode;
import com.xzy.community.exception.CustomizeException;
import com.xzy.community.mapper.QuestionAddCountMapper;
import com.xzy.community.mapper.QuestionMapper;
import com.xzy.community.mapper.UserMapper;
import com.xzy.community.model.Question;
import com.xzy.community.model.QuestionExample;
import com.xzy.community.model.User;
import com.xzy.community.model.UserExample;
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
    private QuestionAddCountMapper questionAddCountMapper;

    public PaginationDTO list( String search,Integer page, Integer size) {

        if(StringUtils.isNotBlank(search)){
            String[] searchs=search.split(" ");
            search= Arrays.stream(searchs).collect(Collectors.joining("|"));
        }

        QuestionQueryDTO questionQueryDTO=new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        questionQueryDTO.setPage(page);
        questionQueryDTO.setSize(size);
        Integer totalCount=questionAddCountMapper.countBySearch(questionQueryDTO);

        Integer offSize=size*(page-1);
        QuestionExample example=new QuestionExample();
        example.setOrderByClause("gmt_create desc");
        List<Question> questionList=questionAddCountMapper.selectBySearch(questionQueryDTO);
//        List<QuestionDTO> questionDTOList=new LinkedList<>();

//        PaginationDTO<QuestionDTO> paginationDTO=new PaginationDTO<QuestionDTO>();

//        for(Question question:questionList){
//            User user=userMapper.selectByPrimaryKey(question.getCreator());
//            QuestionDTO questionDTO=new QuestionDTO();
//            BeanUtils.copyProperties(question,questionDTO);
//            questionDTO.setUser(user);
//            questionDTOList.add(questionDTO);
//        }
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
        questionAddCountMapper.addViewCount(question);
    }

    public PaginationDTO listByTag(String tag, Integer page, Integer size) {
        Integer offSize=size*(page-1);
        QuestionExample example=new QuestionExample();
        example.createCriteria().andTagLike("%"+tag+"%");
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
        example.createCriteria().andTagLike("%"+tag+"%");
        Integer totalCount=(int)questionMapper.countByExample(example);
        paginationDTO.setPagination(totalCount,page,size);
        return paginationDTO;
    }

    public List<Question> listByTags(String[] tags) {

        List<QuestionDTO> likeQuestions=new LinkedList<>();
        for (String tag:tags) {
            PaginationDTO<QuestionDTO> paginationDTO = listByTag(tag, 1, 10);
            likeQuestions.addAll(paginationDTO.getData());
        }
        Set<Long> collect = likeQuestions.stream().map(questionDTO -> questionDTO.getId()).collect(Collectors.toSet());
        List<Long> questionIds=new LinkedList<>();
        questionIds.addAll(collect);
        QuestionExample questionExample=new QuestionExample();
        questionExample.createCriteria().andIdIn(questionIds);
        questionExample.setOrderByClause("gmt_create desc");
        List<Question> questions = questionMapper.selectByExample(questionExample);
        if(questions.size()>11){
            questions.subList(0,10);
        }
        return questions;
    }
}
