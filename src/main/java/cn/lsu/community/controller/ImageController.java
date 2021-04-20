package cn.lsu.community.controller;

import cn.lsu.community.dto.FileDTO;
import cn.lsu.community.dto.ResultDTO;
import cn.lsu.community.entity.User;
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
public class ImageController {

    @Resource
    private UserService userService;
    /**
     * 图片上传
     */
    @PostMapping("/image/upload")
    @ResponseBody
    public ResultDTO upload(@RequestParam("image") MultipartFile file,@RequestParam("flag")Integer flag,Integer id,HttpServletRequest request) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        if(ObjectUtils.isEmpty(user)){
            return ResultDTO.errorOf(501,"你未登录，请先登录！");
        }
        if (!ImageUtils.checkFileSize(file.getSize(),1,"M")) {
            return ResultDTO.errorOf(502,"文件过大，请上传小于1M的图片");
        }
        String imgPath = ImageUtils.upload(file,"user");
        if (flag != null){
            if (flag == 1){
                userService.updateAvatarById(user.getId(),imgPath);
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
}
