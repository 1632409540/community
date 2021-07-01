package cn.lsu.community.service.Impl;

import cn.lsu.community.base.BaseService;
import cn.lsu.community.dto.HotTopicDTO;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.dto.QuestionDTO;
import cn.lsu.community.entity.*;
import cn.lsu.community.enums.NotificationType;
import cn.lsu.community.exception.CustomizeErrorCode;
import cn.lsu.community.exception.CustomizeException;
import cn.lsu.community.dto.QuestionQueryDTO;
import cn.lsu.community.mapper.*;
import cn.lsu.community.service.NotificationService;
import cn.lsu.community.service.QuestionService;
import cn.lsu.community.utils.MailUtils;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
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
    @Resource
    private UserLikeMapper userLikeMapper;
    @Resource
    private NotificationService notificationService;

    //从ES中搜索结果并高亮显示
    @Override
    public List<Question> searchFromEs(String content) {
        List<Question> result = new ArrayList<>();
        // 创建搜索请求
        SearchRequest searchRequest = new SearchRequest("school_blog");
        // 创建搜索对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 设置查询条件
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(content, "title", "description"))
                .from(0)
                .size((int) getEsDocCount())
                .highlighter(new HighlightBuilder().field("*").requireFieldMatch(false).preTags("<span style='color:red;font-weight:500'>").postTags("</span>"));

        searchRequest.types("question").source(searchSourceBuilder);

        // 执行搜索
        SearchResponse response = null;
        try {
            response = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            Question question = new Question();
            question.setId((Long) map.get("id"));
            question.setTitle(map.get("title").toString());
            question.setCreateDate(new Date((Long) map.get("createDate")));
            question.setLastModified(new Date((Long) map.get("lastModified")));
            question.setCreator((Long)map.get("creator"));
            question.setCommentCount((Integer) map.get("commentCount"));
            question.setViewCount((Integer) map.get("viewCount"));
            question.setLikeCount((Integer) map.get("likeCount"));
            question.setStatus((Integer) map.get("status"));

            Map<String, HighlightField> fields = hit.getHighlightFields();
            // 设置标题高亮
            if (fields.containsKey("title")) {
                question.setTitle(fields.get("title").fragments()[0].toString());
            }
            // 设置摘要高亮
            if (fields.containsKey("description")) {
                question.setDescription(fields.get("description").fragments()[0].toString());
            }
            result.add(question);
        }
        return result;
    }

    //初始化ES
    @Override
    public void initEs() {
        // 先清空再添加
        questionRespository.deleteAll();
        Wrapper<Question> wrapper = new EntityWrapper<>();
        wrapper.eq("status",1)
                .orderBy("create_date", false);
        List<Question> questions = baseMapper.selectList(wrapper);


        for (Question question : questions) {
            questionRespository.save(question);
        }
    }

    //获取ES中文档的数量
    @Override
    public long getEsDocCount() {
        // 创建搜索请求
        SearchRequest searchRequest = new SearchRequest("school_blog");
        // 创建搜索对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 设置查询条件
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        searchRequest.types("question").source(searchSourceBuilder);

        // 执行搜索
        SearchResponse response = null;
        try {
            response = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.getHits().totalHits;
    }

    public PaginationDTO list(Long tagId, String search, Integer page, Integer size) {
        if(ObjectUtils.isNotEmpty(search)){
            // 初始化ES
            if (this.getEsDocCount() == 0) {
                log.warn("检测到【ElasticSearch为空】开始初始化..");
                this.initEs();
            }
            List<Question> questions = this.searchFromEs(search);

            List<QuestionDTO> questionDTOList=questions.stream().map(question -> {
                User user=userMapper.selectById(question.getCreator());
                QuestionDTO questionDTO=new QuestionDTO();
                BeanUtils.copyProperties(question,questionDTO);
                List<Tag> tags = tagMapper.selectByQuestionId(question.getId());
                questionDTO.setTags(tags);
                String tag = tags.stream().map(Tag::getName).collect(Collectors.joining(";"));
                questionDTO.setTag(tag);
                questionDTO.setUser(user);

                Wrapper<Comment> commentWrapper = new EntityWrapper<>();
                commentWrapper.eq("parent_id",question.getId());
                questionDTO.setCommentCount(commentMapper.selectCount(commentWrapper));

                Wrapper<QuestionLike> wrapper =new EntityWrapper<>();
                wrapper.eq("question_id",question.getId());
                questionDTO.setLikeCount(questionLikeMapper.selectCount(wrapper));
                return  questionDTO;
            }).collect(Collectors.toList());

            if(ObjectUtils.isNotEmpty(tagId)){
                Tag tag = tagMapper.selectById(tagId);
                questionDTOList = questionDTOList.stream().filter(questionDTO->questionDTO.getTag().lastIndexOf(tag.getName())!=-1).collect(Collectors.toList());
            }
            Integer offSize=size*(page-1);
            List<QuestionDTO> questionDTOS = new ArrayList<>();
            if(questionDTOList.size()>0){
                if((offSize + size)<=questionDTOList.size()-1){
                    questionDTOS = questionDTOList.subList(offSize, (offSize + size));
                }else {
                    questionDTOS = questionDTOList.subList(offSize, questionDTOList.size()-1);
                }
            }
            PaginationDTO<QuestionDTO> paginationDTO=new PaginationDTO<QuestionDTO>();
            paginationDTO.setData(questionDTOS);
            paginationDTO.setPagination(questionDTOList.size(),page,size);
            return paginationDTO;
        }else {
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
                List<Tag> tags = tagMapper.selectByQuestionId(question.getId());
                questionDTO.setTags(tags);
                String tag = tags.stream().map(Tag::getName).collect(Collectors.joining(";"));
                questionDTO.setTag(tag);
                questionDTO.setUser(user);

                Wrapper<Comment> commentWrapper = new EntityWrapper<>();
                commentWrapper.eq("parent_id",question.getId());
                questionDTO.setCommentCount(commentMapper.selectCount(commentWrapper));

                Wrapper<QuestionLike> wrapper =new EntityWrapper<>();
                wrapper.eq("question_id",question.getId());
                questionDTO.setLikeCount(questionLikeMapper.selectCount(wrapper));
                questionDTO.setDescription(null);
                return  questionDTO;
            }).collect(Collectors.toList());
            PaginationDTO<QuestionDTO> paginationDTO=new PaginationDTO<QuestionDTO>();
            paginationDTO.setData(questionDTOList);
            paginationDTO.setPagination(totalCount,page,size);
            return paginationDTO;
        }
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
        Integer totalCount=questionMapper.selectCount(wrapper);
        paginationDTO.setPagination(totalCount,page,size);
        return paginationDTO;
    }

    public QuestionDTO findById(User loginUser, Long id) {
        Question question=questionMapper.selectById(id);
        if(question==null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO=new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user=userMapper.selectById(question.getCreator());
        if(ObjectUtils.isNotEmpty(loginUser)){
            Wrapper<UserLike> wrapper = new EntityWrapper<>();
            wrapper.eq("user_id",loginUser.getId())
                    .eq("liked_user_id", user.getId());
            Integer count = userLikeMapper.selectCount(wrapper);
            if(count>0){
                user.setMyLike(true);
            }else {
                user.setMyLike(false);
            }
        }
        Wrapper<UserLike> wrapper = new EntityWrapper<>();
        wrapper.eq("liked_user_id", user.getId());
        user.setLikeCount(userLikeMapper.selectCount(wrapper));

        Wrapper<Question> questionWrapper = new EntityWrapper<>();
        questionWrapper.eq("creator",user.getId())
                .eq("status",1);
        user.setQuestionCount(questionMapper.selectCount(questionWrapper));
        List<Tag> tags = tagMapper.selectByQuestionId(id);
        questionDTO.setTags(tags);
        String tag = tags.stream().map(Tag::getName).collect(Collectors.joining(";"));
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
                this.updateQuestionTag(questionDTO);
            }catch (Exception e){
                throw new CustomizeException(CustomizeErrorCode.INTEGRAL_NOT_ENOUGH);
            }finally {
                return dbQuestion;
            }
        }else {
            Question question = new Question();
            BeanUtils.copyProperties(questionDTO,question);
            try{
                question.setCreateDate(new Date());
                question.setLastModified(new Date());
                questionMapper.insert(question);
                questionDTO.setId(question.getId());
                this.updateQuestionTag(questionDTO);
                Wrapper<UserLike> userLikeWrapper = new EntityWrapper<>();
                userLikeWrapper.eq("liked_user_id",question.getCreator());
                List<UserLike> userLikes = userLikeMapper.selectList(userLikeWrapper);
                User creator = userMapper.selectById(question.getCreator());
                userLikes.stream().forEach(userLike -> {
                    User user = userMapper.selectById(userLike.getUserId());
                    if (user.getNotifiQuestion() == 1) {
                        Comment comment = new Comment();
                        comment.setCommentator(user.getId());
                        notificationService.createCommentNotify(comment, NotificationType.YOUR_LIKED_USER_HAVE_NEW_QUESTION.getType(), user.getId(), creator.getName(), question.getId(), question.getTitle());
                    }
                    if ( user.getEmailQuestion() == 1) {
                        try {
                            //邮件发送正文
                            InetAddress addr = null;
                            String context = NotificationType.YOUR_LIKED_USER_HAVE_NEW_QUESTION.getName() + "<a href='http://" + addr.getHostAddress()
                                    + ":8090/question/" + question.getId() + "'>" + question.getTitle() + "</a>";
                            //发送邮件
                            MailUtils.sendMail(user.getEmail(), context, "校园博客系统-通知");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }catch (Exception e){
                e.printStackTrace();
                throw new CustomizeException(CustomizeErrorCode.QUESTION_INSERT_ERROR);
            }finally {
                return question;
            }
        }
    }

    private void updateQuestionTag(QuestionDTO questionDTO) {
        Wrapper<QuestionTag> wrapper = new EntityWrapper<>();
        wrapper.eq("question_id",questionDTO.getId());
        questionTagMapper.delete(wrapper);
        for (Tag tag: questionDTO.getTags()){
                QuestionTag questionTag = new QuestionTag();
                questionTag.setQuestionId(questionDTO.getId());
                questionTag.setTagId(tag.getId());
                questionTag.setCreateDate(new Date());
                questionTagMapper.insert(questionTag);
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

    @Override
    public PaginationDTO queryByStatus(Integer status) {
        Wrapper<Question> wrapper = new EntityWrapper<>();
        wrapper.eq("status",status)
                .orderBy("create_date", false);
        List<Question> questionList = baseMapper.selectList(wrapper);
        List<QuestionDTO> questionDTOList=new LinkedList<>();
        PaginationDTO<QuestionDTO> paginationDTO=new PaginationDTO<QuestionDTO>();
        for(Question question:questionList){
            User user=userMapper.selectById(question.getCreator());
            QuestionDTO questionDTO=new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            List<Tag> tags = tagMapper.selectByQuestionId(question.getId());
            questionDTO.setTags(tags);
            String tag = tags.stream().map(Tag::getName).collect(Collectors.joining(";"));
            questionDTO.setTag(tag);
            Wrapper<Comment> commentWrapper = new EntityWrapper<>();
            commentWrapper.eq("parent_id",question.getId());
            questionDTO.setCommentCount(commentMapper.selectCount(commentWrapper));
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        Integer totalCount=questionMapper.selectCount(wrapper);
        paginationDTO.setPagination(totalCount,1,1);
        return paginationDTO;
    }

    @Override
    public Question queryByQuesstion(Question question) {
        return baseMapper.selectOne(question);
    }

    @Override
    public Question queryById(long id) {
        return baseMapper.selectById(id);
    }
}
