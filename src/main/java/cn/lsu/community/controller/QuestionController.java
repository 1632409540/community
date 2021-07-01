package cn.lsu.community.controller;

import cn.lsu.community.base.BaseController;
import cn.lsu.community.dto.CommentDTO;
import cn.lsu.community.dto.HotTopicDTO;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.dto.QuestionDTO;
import cn.lsu.community.entity.Link;
import cn.lsu.community.entity.Question;
import cn.lsu.community.entity.QuestionLike;
import cn.lsu.community.entity.User;
import cn.lsu.community.enums.CommentTypeEnum;
import cn.lsu.community.exception.CustomizeErrorCode;
import cn.lsu.community.exception.CustomizeException;
import cn.lsu.community.mapper.LinkMapper;
import cn.lsu.community.mapper.QuestionLikeMapper;
import cn.lsu.community.service.CommentService;
import cn.lsu.community.service.Impl.CommentServiceImpl;
import cn.lsu.community.service.Impl.QuestionServiceImpl;
import cn.lsu.community.service.QuestionService;
import cn.lsu.community.service.UserService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController extends BaseController {

    @Resource
    private QuestionLikeMapper questionLikeMapper;

    @Resource
    LinkMapper linkMapper;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id")Long id, Model model,HttpServletRequest request){
        User user= (User) request.getSession().getAttribute("user");
        request.getSession().setAttribute("navbarStatus","");
        QuestionDTO questionDTO=questionService.findById(user,id);
        if(questionDTO.getStatus()!=1){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        if(user!=null){
            questionDTO.setMyLike(questionService.checkMyLike(user.getId(),questionDTO.getId()));
        }
        List<CommentDTO> commentDTOList =commentService.findCommentsById(user,id, CommentTypeEnum.QUESTION);
        model.addAttribute("question",questionDTO);
        model.addAttribute("tags",questionDTO.getTags());
        model.addAttribute("comments",commentDTOList);
        List<Question> likeQuestions= questionService.listLikeQuestions(id,questionDTO.getTags());
        model.addAttribute("likeQuestions",likeQuestions);
        questionService.addViewCount(id);
        List<Link> links = linkMapper.selectList(new EntityWrapper<>());
        model.addAttribute("links",links);
        return "question";
    }

    @GetMapping("/likeQuestion")
    public String likeQuestion(@RequestParam(name ="questionId") Long questionId, HttpServletRequest request){
        User user= (User) request.getSession().getAttribute("user");
        QuestionLike questionLike = new QuestionLike();
        questionLike.setQuestionId(questionId);
        questionLike.setUserId(user.getId());
        questionLike.setCreateDate(new Date());
        questionLikeMapper.insert(questionLike);
        questionService.addLikeCount(questionId);
        QuestionDTO questionDTO = questionService.findById(null, questionId);
        User addUser = userService.findById(questionDTO.getCreator());
        userService.updateUserIntegral(addUser,user,20);
        rabbitTemplate.convertAndSend("es","question.save", questionId.toString());
        return "redirect:/question/"+ questionId;
    }

    @GetMapping("/cancelLikeQuestion")
    public String unLikeQuestion(@RequestParam(name ="questionId") Long questionId, HttpServletRequest request){
        User user= (User) request.getSession().getAttribute("user");
        Wrapper<QuestionLike> wrapper =new EntityWrapper<>();
        wrapper.eq("question_id",questionId)
                .eq("user_id",user.getId());
        questionLikeMapper.delete(wrapper);
        questionService.delLikeCount(questionId);
        rabbitTemplate.convertAndSend("es","question.save", questionId.toString());
        return "redirect:/question/"+ questionId;
    }

    @GetMapping("/changeLikeUser")
    public String likeQuestion(@RequestParam(name ="questionId") Long questionId,@RequestParam(name ="userId") Long userId, HttpServletRequest request){
        User user= (User) request.getSession().getAttribute("user");
        userService.changeLikeUser(user.getId(),userId);
        rabbitTemplate.convertAndSend("es","question.save", questionId.toString());
        return "redirect:/question/"+ questionId;
    }

    @GetMapping("/admin/questions")
    public String index(@RequestParam(name = "status",defaultValue = "1",required = false)Integer status, Model model) {
        PaginationDTO paginationDTO=questionService.queryByStatus(status);
        request.getSession().setAttribute("questionStatus",status);
        model.addAttribute("paginationDTO",paginationDTO);
        if(status==1){
            return "admin/publish";
        }else if(status == 0){
            return "admin/draft";
        }else {
            return "admin/deleteLight";
        }
    }

    @GetMapping("/admin/question/edit")
    public String questionEdit(@RequestParam(name = "id")Long id, Model model) {
        QuestionDTO question = questionService.findById(null, id);
        model.addAttribute("id",question.getId());
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("status",question.getStatus());
        model.addAttribute("question",question);
        model.addAttribute("selectTags", tagService.getTagTypes());
        return "admin/edit";
    }

    @RequestMapping("/admin/updateStatus")
    public String questionStatus(@RequestParam(name = "id")Long id,@RequestParam(name = "status")Integer status, Model model) {
        Integer questionStatus = (Integer) request.getSession().getAttribute("questionStatus");
        if(status !=-1){
            QuestionDTO questionDTO = questionService.findById(null,id);
            questionDTO.setStatus(status);
            questionDTO.setLastModified(new Date());
            questionService.createOrUpdate(questionDTO);
        }else {
            questionService.deleteById(id);
        }
        if(status == 1){
            rabbitTemplate.convertAndSend("es","question.save", id.toString());
        }else {
            rabbitTemplate.convertAndSend("es","question.delete", id.toString());
        }
        return "redirect:/admin/questions?status="+questionStatus;
    }
}
