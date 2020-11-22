package com.itheima.health.Exception;

/**
 * <p>
 * 自定异常:
 * 1. 区分是由系统抛出还是由程序员抛出
 * 2. 封装友好的提示
 * 3. 终止已经不符合业务逻辑代码的继续执行
 * </p>
 *
 * @author: Eric
 * @since: 2020/11/22
 */
public class MyException extends RuntimeException {
    public MyException(String msg){
        super(msg);
    }
}
