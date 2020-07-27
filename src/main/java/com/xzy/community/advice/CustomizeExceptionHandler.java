package com.xzy.community.advice;

import com.alibaba.fastjson.JSON;
import com.xzy.community.dto.ResultDTO;
import com.xzy.community.exception.CustomizeErrorCode;
import com.xzy.community.exception.CustomizeException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

@ControllerAdvice
public class CustomizeExceptionHandler {

    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable exception,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response){

        String contentType = request.getContentType();
        if ("application/json".equals(contentType)) {
            //返回json
            ResultDTO resultDTO = null;
            if (exception instanceof CustomizeException) {
                resultDTO = ResultDTO.errorOf((CustomizeException) exception);
            } else {
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYSTEM_ERROR);
            }
            try {
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/json");
                response.setStatus(200);
                Writer writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException e) {
                model.addAttribute("message", CustomizeErrorCode.WRITE_RESULT_ERROR.getMessage());
            }
            return null;
        }else {
            ResultDTO resultDTO = null;
            if (exception instanceof CustomizeException) {
                resultDTO = ResultDTO.errorOf((CustomizeException) exception);
            } else {
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYSTEM_ERROR);
            }
            model.addAttribute("message",resultDTO.getMessage());
        }
        return new ModelAndView("error");
    }
}
