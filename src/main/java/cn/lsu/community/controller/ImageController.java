package cn.lsu.community.controller;

import cn.lsu.community.dto.FileDTO;
import cn.lsu.community.utils.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@Slf4j
public class ImageController {

    @PostMapping("/image/upload")
    @ResponseBody
    private FileDTO upload(@RequestParam("editormd-image-file")MultipartFile file, HttpServletRequest request) throws IOException {
        //上传图片
        String url = ImageUtils.upload(file,"a");
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
