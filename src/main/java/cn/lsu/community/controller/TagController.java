package cn.lsu.community.controller;

import cn.lsu.community.base.BaseController;
import cn.lsu.community.dto.HotTopicDTO;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.entity.QuestionLike;
import cn.lsu.community.entity.Tag;
import cn.lsu.community.entity.User;
import cn.lsu.community.service.QuestionService;
import cn.lsu.community.service.TagService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
public class TagController extends BaseController {

    @GetMapping("/tags")
    public String index(@RequestParam(name ="search",required = false) String search,
                        @RequestParam(name = "page",defaultValue = "1",required = false)Integer page,
                        @RequestParam(name = "size",defaultValue = "10",required = false)Integer size,
                        HttpServletRequest request,
                        Model model) {
        PaginationDTO paginationDTO=tagService.list(search,page,size);
        model.addAttribute("search", search);
        model.addAttribute("paginationDTO",paginationDTO);
        request.getSession().setAttribute("navbarStatus","tags");
        return "tags";
    }

    @GetMapping("/changeLikeTag")
    public String changeLikeTag(@RequestParam(name ="tagId") Long tagId, HttpServletRequest request){
        User user= (User) request.getSession().getAttribute("user");
        request.getSession().setAttribute("navbarStatus","");
        tagService.changeLikeTag(user,tagId);
        return "redirect:/?tagId="+ tagId;
    }
}
