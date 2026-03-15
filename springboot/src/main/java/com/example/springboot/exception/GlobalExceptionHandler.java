package com.example.springboot.exception;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example.springboot.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice(basePackages = "com.example.springboot.controller")  //指定拦截的包<触发作用>（业务代码遇到错误情况，开发者会主动抛出Exception）
public class GlobalExceptionHandler {

    private static final Log log = LogFactory.get(); //定义一个静态log对象，获取日志信息


    //统一异常处理@ExceptionHandler,主要用于Exception
    @ExceptionHandler(Exception.class)  //<拦截作用>（拦截所有未定义的异常，并执行下边定义的方法）
    @ResponseBody//返回json串
    public Result error(HttpServletRequest request, Exception e) {
        log.error("异常信息：", e);
        return Result.error();
    }

    @ExceptionHandler(CustomException.class)  //<拦截作用>（拦截自定义的CustomException类异常，并执行下边定义的方法）
    @ResponseBody//返回json串
    public Result customError(HttpServletRequest request, CustomException e) {
        return Result.error(e.getMsg());
    }

}
