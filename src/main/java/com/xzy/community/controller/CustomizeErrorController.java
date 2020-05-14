package com.xzy.community.controller;

import com.xzy.community.exception.CustomizeErrorCode;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;

@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class CustomizeErrorController implements ErrorController {

    @Override
    public String getErrorPath() {
        return "error";
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    private ModelAndView errorHtml(HttpServletRequest request,
                                   Model model){
        HttpStatus status=getStatus(request);
        if(status.is4xxClientError()){
            model.addAttribute("message",CustomizeErrorCode.REQUEST_ERROR.getMessage());
        }
        if(status.is5xxServerError()){
            model.addAttribute("message", CustomizeErrorCode.SYSTEM_ERROR.getMessage());
        }
        return new ModelAndView("error");
    }

    private HttpStatus getStatus(HttpServletRequest request){
        Integer statusCode= (Integer) request.getAttribute("javax.servlet.error.status_code");
        if(statusCode==null){
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        }catch (Exception e){
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
