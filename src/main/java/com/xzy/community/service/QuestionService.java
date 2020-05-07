package com.xzy.community.service;

import com.xzy.community.dto.PaginationDTO;
import com.xzy.community.dto.QuestionDTO;
import com.xzy.community.mapper.QuestionMapper;
import com.xzy.community.mapper.UserMapper;
import com.xzy.community.model.Question;
import com.xzy.community.model.QuestionExample;
import com.xzy.community.model.User;
import com.xzy.community.model.UserExample;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;

    public PaginationDTO list(Integer page, Integer size) {
        Integer offSize=size*(page-1);
        QuestionExample example=new QuestionExample();
        example.setOrderByClause("gmt_create desc");
        List<Question> questionList=questionMapper.selectByExampleWithBLOBsWithRowbounds(example,new RowBounds(offSize,size));
        List<QuestionDTO> questionDTOList=new LinkedList<>();
        PaginationDTO paginationDTO=new PaginationDTO();
        for(Question question:questionList){
            UserExample userExample=new UserExample();
            User user=userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO=new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        Integer totalCount=(int)questionMapper.countByExample(new QuestionExample());
        paginationDTO.setPagination(totalCount,page,size);
        return paginationDTO;
    }

    public PaginationDTO list(Integer id, Integer page, Integer size) {
        Integer offSize=size*(page-1);
        QuestionExample example=new QuestionExample();
        example.createCriteria().andCreatorEqualTo(id);
        example.setOrderByClause("gmt_create desc");
        List<Question> questionList=questionMapper.selectByExampleWithBLOBsWithRowbounds(example,new RowBounds(offSize,size));
        List<QuestionDTO> questionDTOList=new LinkedList<>();
        PaginationDTO paginationDTO=new PaginationDTO();
        for(Question question:questionList){
            User user=userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO=new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        QuestionExample example2=new QuestionExample();
        example.createCriteria().andIdEqualTo(id);
        Integer totalCount=(int)questionMapper.countByExample(example);
        paginationDTO.setPagination(totalCount,page,size);
        return paginationDTO;
    }

    public QuestionDTO findById(Integer id) {
        Question question=questionMapper.selectByPrimaryKey(id);
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
            example.createCriteria().andIdEqualTo(question.getId());
            questionMapper.updateByExampleSelective(updateQuestion,example);
        }else {
            questionMapper.insert(question);
        }
    }
}
