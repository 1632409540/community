package cn.lsu.community.controller;

import cn.lsu.community.base.BaseController;
import cn.lsu.community.dto.HotTopicDTO;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.dto.ResultDTO;
import cn.lsu.community.entity.*;
import cn.lsu.community.mapper.TagMapper;
import cn.lsu.community.mapper.TagTypeMapper;
import cn.lsu.community.service.QuestionService;
import cn.lsu.community.service.TagService;
import cn.lsu.community.utils.ImageUtils;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
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
public class TagController extends BaseController {

    @Resource
    private TagMapper tagMapper;

    @Resource
    private TagTypeMapper tagTypeMapper;

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

    @RequestMapping("/admin/tags")
    public String index(Model model) {
        List<TagType> tagTypes = tagService.getTagTypes();
        PaginationDTO tags = tagService.queryAll();
        model.addAttribute("tagTypes", tagTypes);
        model.addAttribute("paginationDTO",tags);
        return "admin/tagSet";
    }

    /**
     * 添加标签
     */
    @PostMapping("/admin/addTag")
    @ResponseBody
    public ResultDTO addTag(Tag tag){
        // 检查是否已经创建过这个标签
        Tag tag1 = tagService.selectByName(tag.getName());
        if (ObjectUtils.isNotEmpty(tag1)){
            log.warn("【失败】添加标签，该名称已被占用");
            return ResultDTO.errorOf(501,"【失败】添加标签，该名称已被占用");
        }
        // 为标签设置随机封面
        tag.setImageUrl(ImageUtils.getRandomFace());
        tag.setCreateDate(new Date());
        if (tagMapper.insert(tag)>0) {
            log.warn("【成功】添加标签");
            return ResultDTO.successOf();
        }else {
            log.warn("【失败】添加标签");
            return ResultDTO.errorOf(500,"【失败】添加标签");
        }
    }

    @GetMapping("/admin/tag")
    public String tag(@JsonSerialize(using = ToStringSerializer.class)@RequestParam("id")Long id,Model model){
        log.warn("请求【编辑标签】页面");
        // 如果id为空那么重定向到所有标签页面
        if (id == null){
            return "redirect:/admin/tags";
        }
        // 获取此标签
        Tag tag = tagMapper.selectById(id);
        if (tag == null){
            return "redirect:/admin/tags";
        }
        HotTopicDTO hotTopicDTO = new HotTopicDTO();
        BeanUtils.copyProperties(tag,hotTopicDTO);
        TagType tagType = tagTypeMapper.selectById(tag.getTagTypeId());
        hotTopicDTO.setTagType(tagType);
        List<TagType> tagTypes = tagService.getTagTypes();
        model.addAttribute("tagTypes", tagTypes);
        model.addAttribute("editTag",hotTopicDTO);
        return "admin/tag";
    }

    /**
     * 更新标签
     */
    @PostMapping("/admin/updateTag")
    @ResponseBody
    public ResultDTO updateTag(Tag tag){
        // 检查是否已经创建过这个标签
        Tag tag1 = tagService.selectByName(tag.getName());
        if (ObjectUtils.isNotEmpty(tag1)){
            log.warn("【失败】添加标签，该名称已被占用");
            return ResultDTO.errorOf(502,"【失败】添加标签，该名称已被占用");
        }
        if (tagMapper.updateById(tag)>0) {
            log.warn("【成功】更新标签");
            return ResultDTO.successOf();
        }else {

            log.warn("【失败】更新标签");
            return ResultDTO.errorOf(500,"【失败】更新标签");
        }
    }

    /**
     * 删除标签
     */
    @GetMapping("/admin/deleteTag")
    public String deleteTag(@JsonSerialize(using = ToStringSerializer.class)@RequestParam("id")Long id){
        if (id != null){
            if (tagMapper.deleteById(id)>0) {
                log.warn("【成功】删除标签");
            }else {
                log.warn("【失败】删除标签");
            }
        }
        return "redirect:/admin/tags";
    }
}
