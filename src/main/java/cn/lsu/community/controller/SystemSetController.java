package cn.lsu.community.controller;

import cn.lsu.community.base.BaseController;
import cn.lsu.community.dto.HotTopicDTO;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.dto.ResultDTO;
import cn.lsu.community.entity.Link;
import cn.lsu.community.entity.SystemSet;
import cn.lsu.community.entity.User;
import cn.lsu.community.mapper.LinkMapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
@Slf4j
public class SystemSetController extends BaseController {

    @Resource
    LinkMapper linkMapper;

    @RequestMapping("/admin/systemSet")
    public String adminSystemSet() {
        return "/admin/systemSet";
    }

    @RequestMapping("/admin/link")
    public String adminLink(Model model) {
        List<Link> links = linkMapper.selectList(new EntityWrapper<>());
        model.addAttribute("links",links);
        return "admin/link";
    }

    @PostMapping("/admin/updateSystemTitle")
    public ResultDTO updateSystemTitle(@RequestParam(name = "title") String title) {
        SystemSet systemSet = (SystemSet) request.getSession().getAttribute("systemSet");
        systemSet.setTitle(title);
        systemSetService.updateBySystemSetId(systemSet);
        request.getSession().setAttribute("systemSet",null);
        return ResultDTO.successOf();
    }

    @PostMapping("/admin/updateSystemPublicTitle")
    public ResultDTO updateSystemPublicTitle(@RequestParam(name = "title") String title) {
        SystemSet systemSet = (SystemSet) request.getSession().getAttribute("systemSet");
        systemSet.setPublicTitle(title);
        systemSetService.updateBySystemSetId(systemSet);
        request.getSession().setAttribute("systemSet",null);
        return ResultDTO.successOf();
    }
    /**
     * 添加友情链接
     */
    @PostMapping("/admin/addLink")
    @ResponseBody
    public ResultDTO addLink(Link link){
        link.setCreateDate(new Date());
        if (linkMapper.insert(link)>0) {
            log.warn("【成功】添加友情链接");
            return ResultDTO.successOf();
        }else {
            log.warn("【失败】添加友情链接");
            return ResultDTO.errorOf(500,"【失败】添加友情链接");
        }
    }

    /**
     * 删除友情链接
     */
    @GetMapping("/admin/deleteLink")
    @ResponseBody
    public ResultDTO deleteLink(@JsonSerialize(using = ToStringSerializer.class)@RequestParam("id")Long id){
        if (linkMapper.deleteById(id)>0) {
            log.warn("【成功】删除友情链接，ID："+id);
            return ResultDTO.successOf();
        }else {
            log.warn("【失败】删除友情链接，ID："+id);
            return ResultDTO.errorOf(500,"【失败】删除友情链接，ID："+id);
        }
    }


    /**
     * 重建ElasticSearch
     */
    @GetMapping("/admin/resetEs")
    @ResponseBody
    public ResultDTO resetElasticSearch(){
        log.warn("【ElasticSearch】开始保存数据到ES");
        long startTime = System.currentTimeMillis();
        questionService.initEs();
        long endTime = System.currentTimeMillis();
        log.warn("【ElasticSearch】结束保存文章数据到ES，"+"程序运行时间：" + (endTime - startTime) + "ms");
        return ResultDTO.successOf(endTime - startTime);
    }


    /**
     * 清空Redis
     */
    @GetMapping("/admin/cleanRedis")
    @ResponseBody
    public ResultDTO cleanRedis(){
        log.warn("【Redis】开始清空Redis");
        long startTime = System.currentTimeMillis();
        redisService.flush();
        long endTime = System.currentTimeMillis();
        log.warn("【Redis】清空完成，"+"程序运行时间：" + (endTime - startTime) + "ms");
        return ResultDTO.successOf(endTime - startTime);
    }
}
