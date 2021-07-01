package cn.lsu.community.controller;

import cn.lsu.community.base.BaseController;
import cn.lsu.community.dto.FileDTO;
import cn.lsu.community.dto.ResultDTO;
import cn.lsu.community.entity.SystemSet;
import cn.lsu.community.entity.Tag;
import cn.lsu.community.entity.User;
import cn.lsu.community.mapper.TagMapper;
import cn.lsu.community.service.UserService;
import cn.lsu.community.utils.ImageUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@Slf4j
public class ImageController extends BaseController {

    @Resource
    private UserService userService;

    @Resource
    private TagMapper tagMapper;
    /**
     * 图片上传
     */
    @PostMapping("/image/upload")
    @ResponseBody
    public ResultDTO upload(@RequestParam("image") MultipartFile file,@RequestParam("flag")Integer flag,Long id,HttpServletRequest request) throws IOException {
        if(ObjectUtils.isEmpty(id)){
            if(ObjectUtils.isEmpty(loginUser)){
                return ResultDTO.errorOf(501,"你未登录，请先登录！");
            }
        }
        if (!ImageUtils.checkFileSize(file.getSize(),1,"M")) {
            return ResultDTO.errorOf(502,"文件过大，请上传小于1M的图片");
        }
        String imgPath = ImageUtils.upload(file,"user");
        if (flag != null){
            if (flag == 1){
                userService.updateAvatarById(loginUser.getId(),imgPath);
            }
            if(flag == 2){
                userService.updateAvatarById(id,imgPath);
            }
            if(flag == 8){
                Tag tag = tagMapper.selectById(id);
                tag.setImageUrl(imgPath);
                tagMapper.updateById(tag);
            }
        }
        // 返回结果
        return ResultDTO.successOf(imgPath);
    }

    @PostMapping("/image/question")
    @ResponseBody
    private FileDTO upload(@RequestParam("editormd-image-file")MultipartFile file, HttpServletRequest request) throws IOException {
        //上传图片
        String url = ImageUtils.upload(file,"q");
        //返回图片链接
        FileDTO fileDTO=new FileDTO();
        if (url != null){
            fileDTO.setSuccess(1);
            fileDTO.setUrl(url);
            log.warn("【成功】文章上传图片到Gitee图床，URL："+url);
        }else {
            log.warn("【失败】文章上传图片到Gitee图床");
        }
        return fileDTO;
    }
    /**
     * 图片上传
     */
    @PostMapping("/admin/image/upload")
    @ResponseBody
    public ResultDTO adminUpload(@RequestParam("image") MultipartFile file,@RequestParam("flag")Integer flag) throws IOException {

        if (!ImageUtils.checkFileSize(file.getSize(),1,"M")) {
            return ResultDTO.errorOf(502,"文件过大，请上传小于1M的图片");
        }
        String imgPath = ImageUtils.upload(file,"admin");
        if (flag != null){
            SystemSet systemSet = (SystemSet) request.getSession().getAttribute("systemSet");
            if (flag == 3){
                systemSet.setSystemLogo(imgPath);
            }else if(flag == 4){
                systemSet.setBackground(imgPath);
            }else if(flag == 5){
                systemSet.setPublicWechat(imgPath);
            }else if(flag == 6){
                systemSet.setPublicMicroblog(imgPath);
            }else if(flag == 7){
                systemSet.setPublicQq(imgPath);
            }
            systemSetService.updateBySystemSetId(systemSet);
            request.getSession().setAttribute("systemSet",null);
        }
        // 返回结果
        return ResultDTO.successOf(imgPath);
    }

}
