package cn.lsu.community.controller;

import cn.lsu.community.dto.HotTopicDTO;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.entity.Tag;
import cn.lsu.community.service.QuestionService;
import cn.lsu.community.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;
    @Resource
    private TagService tagService;

    @GetMapping("/")
    public String index(@RequestParam(name ="search",required = false) String search,
                        @RequestParam(name ="tagId",required = false) Long tagId,
                        @RequestParam(name = "page",defaultValue = "1",required = false)Integer page,
                        @RequestParam(name = "size",defaultValue = "10",required = false)Integer size,
                        HttpServletRequest request,
                        Model model
                           ) {
        Tag tag = tagService.selectById(tagId);
        PaginationDTO paginationDTO=questionService.list(tagId,search,page,size);
        List<HotTopicDTO> hotTopicDTOs=questionService.getHotTopic();
        model.addAttribute("navbarStatus", "index");
        model.addAttribute("tag", tag);
        model.addAttribute("tagId", tagId);
        model.addAttribute("search", search);
        model.addAttribute("hotTopics", hotTopicDTOs);
        model.addAttribute("paginationDTO",paginationDTO);
        request.getSession().setAttribute("navbarStatus","index");
        return "index";
    }
}
