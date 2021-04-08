package com.mrbai.config;

import com.mrbai.untils.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author: MrBai
 * @date: 2021-04-01 13:48
 **/

@ControllerAdvice
public class ValidateFailHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        System.out.println("handleBindException");
        ResponseEntity res = excptionCheck(ex);
        if (res != null) {
            return res;
        }
        return super.handleBindException(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        System.out.println("handleMethodArgumentNotValid");
        ResponseEntity res = excptionCheck(ex);
        if (res != null) {
            return res;
        }
        return super.handleMethodArgumentNotValid(ex,headers,status,request);
    }

    private ResponseEntity<Object> excptionCheck(BindException ex) {
        if (ex.hasErrors()) {
            ApiResponse res = new ApiResponse();
            res.setCode(244);
            for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
                String msg = fieldError.getDefaultMessage();
                res.setMsg(msg);
                break;
            }
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return null;
    }

    // 运行时异常处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiResponse runTimeExceptionHandler(RuntimeException e) {
        System.out.println("========");
        System.out.println(e.getMessage());
        return new ApiResponse(200, e.getMessage(), null);
    }

}
