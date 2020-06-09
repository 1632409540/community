package com.xzy.community.controller;

import com.xzy.community.cache.TagCache;
import com.xzy.community.dto.QuestionDTO;
import com.xzy.community.mapper.QuestionMapper;
import com.xzy.community.mapper.UserMapper;
import com.xzy.community.model.Question;
import com.xzy.community.model.User;
import com.xzy.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
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

    @PostMapping("/Publish")
    public String doPublish(
            @RequestParam(value = "title",required = false)String title,
            @RequestParam(value = "description",required = false)String description,
            @RequestParam(value = "tag",required = false)String tag,
            @RequestParam(value = "id",required = false)Long id,
            HttpServletRequest request,
            Model model
    ){
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        model.addAttribute("selectTags", TagCache.get());
        if (title == null || title == "") {
            model.addAttribute("error","标题不能为空！");
            return "publish";
        }
        if (description == null || description == "") {
            model.addAttribute("error","问题补充不能为空！");
            return "publish";
        }
        if (tag == null || tag == "") {
            model.addAttribute("error","标签不能为空！");
            return "publish";
        }
        String s = TagCache.filterInvalid(tag);
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
        Question question=new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreate());
        question.setLikeCount(0);
        question.setViewCount(0);
        question.setCommentCount(0);
        question.setId(id);
        questionService.createOrUpdate(question);
       return "redirect:/";
    }
}
