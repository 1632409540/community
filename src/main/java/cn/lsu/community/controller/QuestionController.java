package cn.lsu.community.controller;

import cn.lsu.community.dto.CommentDTO;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.dto.QuestionDTO;
import cn.lsu.community.enums.CommentTypeEnum;
import cn.lsu.community.service.CommentService;
import cn.lsu.community.service.Impl.CommentServiceImpl;
import cn.lsu.community.service.Impl.QuestionServiceImpl;
import cn.lsu.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class QuestionController {
    @Resource
    private QuestionService questionService;
    @Resource
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id")Long id,
                           @RequestParam(name = "page",defaultValue = "1")Integer page,
                           @RequestParam(name = "size",defaultValue = "11")Integer size,
                            Model model){

        QuestionDTO questionDTO=questionService.findById(id);
        List<CommentDTO> commentDTOList =commentService.findCommentsById(id, CommentTypeEnum.QUESTION);
        model.addAttribute("question",questionDTO);
        String[] tags=questionDTO.getTag().split(",");
        model.addAttribute("tags",tags);
        model.addAttribute("comments",commentDTOList);
        PaginationDTO paginationDTO= questionService.list(id, null,questionDTO.getTag(),page,size);
        model.addAttribute("likeQuestions",paginationDTO);
        questionService.addViewCount(id);
        return "question";
    }

}
