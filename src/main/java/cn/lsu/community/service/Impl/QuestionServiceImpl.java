package cn.lsu.community.service.Impl;

import cn.lsu.community.base.BaseService;
import cn.lsu.community.dto.HotTopicDTO;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.dto.QuestionDTO;
import cn.lsu.community.exception.CustomizeErrorCode;
import cn.lsu.community.exception.CustomizeException;
import cn.lsu.community.dto.QuestionQueryDTO;
import cn.lsu.community.mapper.QuestionMapper;
import cn.lsu.community.mapper.UserMapper;
import cn.lsu.community.entity.Question;
import cn.lsu.community.entity.User;
import cn.lsu.community.service.QuestionService;
import cn.lsu.community.service.UserService;
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

    public PaginationDTO list(Long id, String search, String tag, Integer page, Integer size) {

        Integer offSize=size*(page-1);
        if(StringUtils.isNotEmpty(search)){
            String[] searchs=search.split(" ");
            search= Arrays.stream(searchs).collect(Collectors.joining("|"));
        }
        if(StringUtils.isNotEmpty(tag)){
            String[] tags=tag.split(",");
            tag= Arrays.stream(tags).collect(Collectors.joining("|"));
        }
        QuestionQueryDTO questionQueryDTO=new QuestionQueryDTO();
        questionQueryDTO.setId(id);
        questionQueryDTO.setSearch(search);
        questionQueryDTO.setTag(tag);
        questionQueryDTO.setPage(offSize);
        questionQueryDTO.setSize(size);
        Integer totalCount= questionMapper.countBySearchAndTagExceptId(questionQueryDTO);

        List<Question> questionList= questionMapper.selectBySearchAndTagExceptId(questionQueryDTO);

        List<QuestionDTO> questionDTOList=questionList.stream().map(question -> {
            User user=userMapper.selectById(question.getCreator());
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
        questionDTO.setUser(user);
        return questionDTO;
    }

    public Question createOrUpdate(Question question) {
        Question dbQuestion= questionMapper.selectById(question.getId());
        if(dbQuestion!=null){
            Question updateQuestion=new Question();
            updateQuestion.setId(dbQuestion.getId());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            try{
                questionMapper.updateById(updateQuestion);
            }catch (Exception e){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }finally {
                return dbQuestion;
            }

        }else {
            question.setId(null);
            try{
                questionMapper.insert(question);
            }catch (Exception e){
                e.printStackTrace();
                throw new CustomizeException(CustomizeErrorCode.QUESTION_INSERT_ERROR);
            }finally {
                return question;
            }
        }
    }
    public void addViewCount(Long id) {
        Question question=new Question();
        question.setId(id);
        question.setViewCount(1);
        questionMapper.addViewCount(question);
    }

    public HotTopicDTO getHotTopic() {
        HotTopicDTO hotTopicDTO=new HotTopicDTO();
        List<Question> questions = questionMapper.selectList(new EntityWrapper<>());
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
            int count = questionMapper.getCommentCountByTag("%"+entry.getKey()+"%");
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
