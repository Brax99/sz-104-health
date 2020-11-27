package com.itheima.health.controller;

import com.itheima.health.Exception.MyException;
import com.itheima.health.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HealExceptionAdvice {

    private static final Logger log = LoggerFactory.getLogger(HealExceptionAdvice.class);

    /***
     * catch(MyException)
     */
    @ExceptionHandler(MyException.class)
    public Result handleMyException(MyException e){
        return new Result(false, e.getMessage());
    }

    /***
     * catch(MyException)
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        //e.printStackTrace();
        //System.out.println();
        log.error("发知未知异常",e);
        return new Result(false, "发知未知异常，请稍后重试");
    }
}
