package cn.lsu.community.controller;

import cn.lsu.community.dto.CommentCreateDTO;
import cn.lsu.community.dto.CommentDTO;
import cn.lsu.community.dto.ResultDTO;
import cn.lsu.community.enums.CommentTypeEnum;
import cn.lsu.community.exception.CustomizeErrorCode;
import cn.lsu.community.entity.Comment;
import cn.lsu.community.entity.User;
import cn.lsu.community.service.CommentService;
import cn.lsu.community.service.Impl.CommentServiceImpl;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {

    @Resource
    private CommentService commentService;

    @ResponseBody
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request) {

        User user= (User) request.getSession().getAttribute("user");
        if(user==null){
            return ResultDTO.errorOf(CustomizeErrorCode.NOT_LOGIN);
        }
        if(commentCreateDTO==null|| StringUtils.isNullOrEmpty(commentCreateDTO.getContent())){
            return ResultDTO.errorOf(CustomizeErrorCode.COMMENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0);
        comment.setCommentCount(0);
        commentService.insert(comment,user);
        return ResultDTO.successOf();
    }

    @ResponseBody
    @RequestMapping(value = "/comment/{id}", method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comments(@PathVariable(name = "id")Long id){
        List<CommentDTO> commentDTOS = commentService.findCommentsById(id, CommentTypeEnum.COMMENT);
        return ResultDTO.successOf(commentDTOS);
    }

    @ResponseBody
    @RequestMapping(value = "/commentThumbsUp/{id}", method = RequestMethod.GET)
    public ResultDTO<Integer> question(@PathVariable(name = "id")Long id){
        Integer likeCount=commentService.addLikeCount(id);
        return ResultDTO.successOf(likeCount);
    }
}
