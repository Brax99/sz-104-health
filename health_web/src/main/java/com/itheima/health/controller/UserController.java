package com.itheima.health.controller;

import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/29  20:46
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/getUsername")
    //用户名回显
    public Result getUsername() {
        // 获取登陆用户的认证信息
        //获取保存在ThreadLocal中的用户信息。
        User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 登陆用户名
        String username = loginUser.getUsername();
        // 返回给前端
        return new Result(true, MessageConstant.GET_USERNAME_SUCCESS, username);
    }

    @RequestMapping("/loginSuccess")
    public Result loginSuccess() {
        return new Result(true, MessageConstant.LOGIN_SUCCESS);
    }

    @RequestMapping("/loginFail")
    public Result loginFail() {
        return new Result(false, "用户名或密码不正确");
    }
}
