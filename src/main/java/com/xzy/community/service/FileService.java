package com.xzy.community.service;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.UUID;

@Service
public class FileService {

    public String upload(InputStream fileStream,String mimeType,String fileName){
        String generatedFileName;
        String[] filePaths=fileName.split("\\.");
        if(filePaths.length>1){
            generatedFileName= UUID.randomUUID().toString()+"."+filePaths[filePaths.length-1];
        }else {
            return null;
        }

        try{
            //文件上传的地址
            String path = ResourceUtils.getURL("classpath:").getPath()+"static/images/upload";
            String realPath = path.replace('/', '\\').substring(1,path.length());

            BufferedInputStream bis=new BufferedInputStream(fileStream);
            BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(realPath+"\\"+generatedFileName));
            int len=0;
            while((len=bis.read())!=-1){
                bos.write(len);
            }
            bos.close();
            bis.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return generatedFileName;
    }
}
