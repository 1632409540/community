package cn.lsu.community.controller;

import cn.lsu.community.dto.QuestionDTO;
import cn.lsu.community.cache.TagCache;
import cn.lsu.community.dto.ResultDTO;
import cn.lsu.community.entity.Question;
import cn.lsu.community.entity.User;
import cn.lsu.community.exception.CustomizeErrorCode;
import cn.lsu.community.service.Impl.QuestionServiceImpl;
import cn.lsu.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Resource
    private QuestionService questionService;

    @GetMapping("/publish/{id}")
    public String editPublish(@PathVariable(name = "id")Long id,
                              Model model){

        QuestionDTO question = questionService.findById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",question.getId());
        model.addAttribute("status", question.getStatus());
        model.addAttribute("selectTags", TagCache.get());
        return "publish";
    }
    @GetMapping("/publish")
    public String publish(Model model){
        model.addAttribute("selectTags", TagCache.get());
        return "publish";
    }

    @ResponseBody
    @PostMapping("/publish")
    public Object doPublish(
            @RequestBody Question question,
            HttpServletRequest request,
            Model model
    ){
        model.addAttribute("question",question);
        model.addAttribute("selectTags", TagCache.get());
        String s = TagCache.filterInvalid(question.getTag());
        if (StringUtils.isNotBlank(s)) {
            return ResultDTO.errorOf(CustomizeErrorCode.ERROR_TAG);
        }
        User user= (User) request.getSession().getAttribute("user");
        if(user==null){
            return ResultDTO.errorOf(CustomizeErrorCode.NOT_LOGIN);
        }
        question.setCreator(user.getId());
        Question saveQuestion = questionService.createOrUpdate(question);
        return ResultDTO.successOf(saveQuestion);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteQuestion/{id}", method = RequestMethod.GET)
    public Object question(@PathVariable(name = "id")Long id){
        questionService.deleteById(id);
        return ResultDTO.successOf();
    }
}
