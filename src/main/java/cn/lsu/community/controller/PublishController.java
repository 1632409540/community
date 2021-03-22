package cn.lsu.community.controller;

import cn.lsu.community.dto.QuestionDTO;
import cn.lsu.community.cache.TagCache;
import cn.lsu.community.dto.ResultDTO;
import cn.lsu.community.entity.Question;
import cn.lsu.community.entity.User;
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
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("selectTags", TagCache.get());
        model.addAttribute("status", question.getStatus());
//        if (title == null || title == "") {
//            model.addAttribute("error","标题不能为空！");
//            return "publish";
//        }
//        if (description == null || description == "") {
//            model.addAttribute("error","问题补充不能为空！");
//            return "publish";
//        }
//        if (tag == null || tag == "") {
//            model.addAttribute("error","标签不能为空！");
//            return "publish";
//        }
        String s = TagCache.filterInvalid(question.getTag());
        System.out.println(s);
        if (StringUtils.isNotBlank(s)) {
            model.addAttribute("error","存在不合法标签！");
            return "publish";
        }
        User user= (User) request.getSession().getAttribute("user");
        if(user==null){
            model.addAttribute("error","用户未登录");
            return "publish";
        }
        question.setCreator(user.getId());
        Question saveQuestion = questionService.createOrUpdate(question);
        return ResultDTO.successOf(saveQuestion);
    }
}
