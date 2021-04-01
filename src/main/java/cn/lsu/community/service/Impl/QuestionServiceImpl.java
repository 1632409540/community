package cn.lsu.community.service.Impl;

import cn.lsu.community.base.BaseService;
import cn.lsu.community.dto.HotTopicDTO;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.dto.QuestionDTO;
import cn.lsu.community.entity.*;
import cn.lsu.community.enums.CommentTypeEnum;
import cn.lsu.community.exception.CustomizeErrorCode;
import cn.lsu.community.exception.CustomizeException;
import cn.lsu.community.dto.QuestionQueryDTO;
import cn.lsu.community.mapper.*;
import cn.lsu.community.service.CommentService;
import cn.lsu.community.service.QuestionService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl extends BaseService<QuestionMapper,Question> implements QuestionService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private QuestionTagMapper questionTagMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private QuestionLikeMapper questionLikeMapper;

    public PaginationDTO list(Long tagId, String search, Integer page, Integer size) {

        Integer offSize=size*(page-1);

        QuestionQueryDTO questionQueryDTO=new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        questionQueryDTO.setPage(offSize);
        questionQueryDTO.setSize(size);
        if(StringUtils.isNotEmpty(search)){
            String[] searchs=search.split(" ");
            search= Arrays.stream(searchs).collect(Collectors.joining("|"));
        }

        Integer totalCount= questionMapper.selectCountByTagAndSearch(tagId,search);

        List<Question> questionList= questionMapper.selectPageByTagAndSearch(tagId,search,offSize,size);

        List<QuestionDTO> questionDTOList=questionList.stream().map(question -> {
            User user=userMapper.selectById(question.getCreator());
            QuestionDTO questionDTO=new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setTags(tagMapper.selectByQuestionId(question.getId()));
            questionDTO.setUser(user);

            Wrapper<Comment> commentWrapper = new EntityWrapper<>();
            commentWrapper.eq("parent_id",question.getId())
                    .eq("type", 1);
            questionDTO.setCommentCount(commentMapper.selectCount(commentWrapper));

            Wrapper<QuestionLike> wrapper =new EntityWrapper<>();
            wrapper.eq("question_id",question.getId());
            questionDTO.setLikeCount(questionLikeMapper.selectCount(wrapper));
            return  questionDTO;
        }).collect(Collectors.toList());

        PaginationDTO<QuestionDTO> paginationDTO=new PaginationDTO<QuestionDTO>();
        paginationDTO.setData(questionDTOList);
        paginationDTO.setPagination(totalCount,page,size);
        return paginationDTO;
    }

    public PaginationDTO list(Long userId, Integer page, Integer size,Integer status) {
        Integer offSize=size*(page-1);
        Wrapper<Question> wrapper = new EntityWrapper<>();
        wrapper.eq("creator",userId)
                .eq("status",status)
                .orderBy("create_date", false);
        List<Question> questionList = baseMapper.selectPage(new RowBounds(offSize,size),wrapper);
        List<QuestionDTO> questionDTOList=new LinkedList<>();
        PaginationDTO<QuestionDTO> paginationDTO=new PaginationDTO<QuestionDTO>();
        for(Question question:questionList){
            User user=userMapper.selectById(question.getCreator());
            QuestionDTO questionDTO=new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        Integer totalCount=questionMapper.selectCount(new EntityWrapper<>());
        paginationDTO.setPagination(totalCount,page,size);
        return paginationDTO;
    }

    public QuestionDTO findById(Long id) {
        Question question=questionMapper.selectById(id);
        if(question==null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO=new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user=userMapper.selectById(question.getCreator());
        List<Tag> tags = tagMapper.selectByQuestionId(id);
        questionDTO.setTags(tags);
        String tag = tags.stream().map(Tag::getName).collect(Collectors.joining(","));
        questionDTO.setTag(tag);
        Wrapper<Comment> commentWrapper=new EntityWrapper<>();
        commentWrapper.eq("parent_id",id);
        questionDTO.setCommentCount(commentMapper.selectCount(commentWrapper));
        questionDTO.setUser(user);
        return questionDTO;
    }

    public Question createOrUpdate(QuestionDTO questionDTO) {
        if(questionDTO.getId()!= null){
            Question dbQuestion= questionMapper.selectById(questionDTO.getId());
            BeanUtils.copyProperties(questionDTO,dbQuestion);
            try{
                questionMapper.updateById(dbQuestion);
                this.createOrUpdateQuestionTag(questionDTO);
            }catch (Exception e){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }finally {
                return dbQuestion;
            }
        }else {
            Question question = new Question();
            BeanUtils.copyProperties(questionDTO,question);
            try{
                question.setCreateDate(new Date());
                questionMapper.insert(question);
                questionDTO.setId(question.getId());
                this.createOrUpdateQuestionTag(questionDTO);
            }catch (Exception e){
                e.printStackTrace();
                throw new CustomizeException(CustomizeErrorCode.QUESTION_INSERT_ERROR);
            }finally {
                return question;
            }
        }
    }

    private void createOrUpdateQuestionTag(QuestionDTO questionDTO) {
        for (Tag tag: questionDTO.getTags()){
            Wrapper<QuestionTag> wrapper = new EntityWrapper<>();
            wrapper.eq("question_id",questionDTO.getId())
                    .eq("tag_id", tag.getId());
            Integer count = questionTagMapper.selectCount(wrapper);
            if (count <= 0) {
                QuestionTag questionTag = new QuestionTag();
                questionTag.setQuestionId(questionDTO.getId());
                questionTag.setTagId(tag.getId());
                questionTag.setCreateDate(new Date());
                questionTagMapper.insert(questionTag);
            }
        }
    }

    public void addViewCount(Long id) {
        Question question=new Question();
        question.setId(id);
        question.setViewCount(1);
        questionMapper.addViewCount(question);
    }

    public List<HotTopicDTO> getHotTopic() {
        LinkedList<HotTopicDTO> data = tagMapper.selectHotTopic();
        Collections.sort(data, new Comparator<HotTopicDTO>() {
            @Override
            public int compare(HotTopicDTO t1, HotTopicDTO t2) {
                int i = t2.getQuestionCount() - t1.getQuestionCount();
                if(i == 0){
                    return t2.getLikeCount() - t1.getLikeCount();
                }
                return i;
            }
        });
        return data;
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

    @Override
    public void deleteById(Long id) {
        Wrapper<QuestionTag> wrapper = new EntityWrapper<>();
        wrapper.eq("question_id",id);
        questionTagMapper.delete(wrapper);
        baseMapper.deleteById(id);
    }

    @Override
    public List<Question> listLikeQuestions(Long id, List<Tag> tags) {
        List<Long> tagIds = new LinkedList<>();
        for (Tag tag : tags) {
            tagIds.add(tag.getId());
        }

        Wrapper<QuestionTag> questionTagWrapper = new EntityWrapper<>();
        questionTagWrapper.ne("question_id", id)
                .in("tag_id",tagIds)
                .groupBy("question_id")
                .orderBy("last_modified",false);
        List<QuestionTag> questionTags = questionTagMapper.selectList(questionTagWrapper);
        List<Long> questionIds = new LinkedList<>();
//        for (QuestionTag questionTag : questionTags) {
//            questionIds.add(questionTag.getId());
//        }
//        List<Question> questions = questionMapper.selectBatchIds(questionIds);
        List<Question> questions = new ArrayList<>();
        for (QuestionTag questionTag : questionTags) {
            questions.add(questionMapper.selectById(questionTag.getQuestionId()));
        }
        return questions;
    }

    @Override
    public void addLikeCount(Long questionId) {
        Question question=new Question();
        question.setId(questionId);
        question.setLikeCount(1);
        questionMapper.addLikeCount(question);
    }

    @Override
    public void delLikeCount(Long questionId) {
        Question question=new Question();
        question.setId(questionId);
        question.setLikeCount(-1);
        questionMapper.addLikeCount(question);
    }

    @Override
    public boolean checkMyLike(Long userId, Long questionId) {
        Wrapper<QuestionLike> wrapper =new EntityWrapper<>();
        wrapper.eq("question_id",questionId)
                .eq("user_id",userId);
        Integer count = questionLikeMapper.selectCount(wrapper);
        if(count>0){
            return true;
        }
        return false;
    }
}
