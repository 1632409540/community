package cn.lsu.community.controller;

import cn.lsu.community.base.BaseController;
import cn.lsu.community.dto.QuestionDTO;
import cn.lsu.community.dto.ResultDTO;
import cn.lsu.community.entity.Question;
import cn.lsu.community.entity.Tag;
import cn.lsu.community.entity.TagType;
import cn.lsu.community.entity.User;
import cn.lsu.community.exception.CustomizeErrorCode;
import cn.lsu.community.service.QuestionService;
import cn.lsu.community.service.TagService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class PublishController extends BaseController {


    @GetMapping("/publish/{id}")
    public String editPublish(@PathVariable(name = "id")Long id, Model model){

        QuestionDTO question = questionService.findById(null, id);
        model.addAttribute("id",question.getId());
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("status",question.getStatus());
        model.addAttribute("question",question);
        model.addAttribute("selectTags", tagService.getTagTypes());
        return "publish";
    }

    @GetMapping("/gotoPublish")
    public String publish(Model model,HttpServletRequest request){
        request.getSession().setAttribute("navbarStatus","publish");
        List<TagType> tagTypes = tagService.getTagTypes();
        model.addAttribute("selectTags", tagTypes);
        return "publish";
    }

    @ResponseBody
    @PostMapping("/publish")
    public Object doPublish(@RequestBody QuestionDTO question, HttpServletRequest request, Model model){
        model.addAttribute("id",question.getId());
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("status",question.getStatus());
        model.addAttribute("question",question);
        model.addAttribute("selectTags", tagService.getTagTypes());
        List<Tag> tags= tagService.filterInvalid(question.getTag());
        if(question.getTitle().length()>25){
            return ResultDTO.errorOf(402,"标题长度不能超过25字！");
        }
        if (ObjectUtils.isEmpty(tags)) {
            return ResultDTO.errorOf(CustomizeErrorCode.ERROR_TAG);
        }
        question.setTags(tags);
        User user= (User) request.getSession().getAttribute("user");
        if(user==null){
            return ResultDTO.errorOf(CustomizeErrorCode.NOT_LOGIN);
        }
        question.setCreator(user.getId());
        Question saveQuestion = questionService.createOrUpdate(question);
        if(question.getStatus()!=null && question.getStatus()==1){
            userService.updateUserIntegral(null,user,20);
            rabbitTemplate.convertAndSend("es","question.save", saveQuestion.getId().toString());
        }else if(question.getStatus()!=null){
            rabbitTemplate.convertAndSend("es","question.delete", saveQuestion.getId().toString());
        }
        return ResultDTO.successOf(saveQuestion);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteQuestion/{id}", method = RequestMethod.GET)
    public Object question(@PathVariable(name = "id")Long id){
        questionService.deleteById(id);
        rabbitTemplate.convertAndSend("es","question.delete", id.toString());
        return ResultDTO.successOf();
    }
}
