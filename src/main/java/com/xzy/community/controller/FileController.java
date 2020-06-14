package com.xzy.community.controller;

import com.xzy.community.dto.FileDTO;
import com.xzy.community.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@Controller
public class FileController {
    @Autowired
    private FileService fileService;

    @RequestMapping("/file/upload")
    @ResponseBody
    private FileDTO upload(HttpServletRequest request){

        MultipartHttpServletRequest multipartRequest= (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("editormd-image-file");
        String fileName=null;
        try {
            fileName=fileService.upload(file.getInputStream(),file.getContentType(),file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileDTO fileDTO=new FileDTO();
        fileDTO.setSuccess(1);
        fileDTO.setUrl("/images/upload/"+fileName);
        return fileDTO;
    }
}
