package cn.lsu.community.controller;

import cn.lsu.community.base.BaseController;
import cn.lsu.community.dto.CommentCreateDTO;
import cn.lsu.community.dto.CommentDTO;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.dto.ResultDTO;
import cn.lsu.community.enums.CommentTypeEnum;
import cn.lsu.community.exception.CustomizeErrorCode;
import cn.lsu.community.entity.Comment;
import cn.lsu.community.entity.User;
import cn.lsu.community.service.CommentService;
import cn.lsu.community.service.Impl.CommentServiceImpl;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
@Slf4j
public class CommentController extends BaseController {

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
        List<CommentDTO> commentDTOS = commentService.findCommentsById(null,id, CommentTypeEnum.COMMENT);
        return ResultDTO.successOf(commentDTOS);
    }

    @ResponseBody
    @RequestMapping(value = "/commentThumbsUp/{id}", method = RequestMethod.GET)
    public ResultDTO<Integer> question(@PathVariable(name = "id")Long id,HttpServletRequest request){
        User user= (User) request.getSession().getAttribute("user");
        if(user==null){
            return ResultDTO.errorOf(CustomizeErrorCode.NOT_LOGIN);
        }
        Integer count = commentService.changeLikeCount(user,id);
        return ResultDTO.successOf(count);
    }

    @RequestMapping("/admin/comments")
    public String adminComment(Model model){
        PaginationDTO paginationDTO = commentService.queryAll();
        model.addAttribute("paginationDTO",paginationDTO);
        return "/admin/comments";
    }

    /**
     * 删除标签
     */
    @GetMapping("/admin/deleteComment")
    @ResponseBody
    public ResultDTO deleteComment(@JsonSerialize(using = ToStringSerializer.class) @RequestParam("id") Long id) {

        if (commentService.deleteCommentById(id) > 0) {
            log.warn("【成功】删除回复");
            return ResultDTO.successOf();
        } else {
            log.warn("【失败】删除回复");
            return ResultDTO.errorOf(500, "【失败】删除回复");
        }

    }
}
