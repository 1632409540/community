package com.xzy.community.advice;

import com.xzy.community.exception.CustomizeException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CustomizeExceptionHandler {
    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable exception, Model model){
        if(exception instanceof CustomizeException){
            model.addAttribute("message",exception.getMessage());
        }else {
            model.addAttribute("message","服务器冒烟了，请稍后再试试！！！");
        }
        return new ModelAndView("error");
    }
}
