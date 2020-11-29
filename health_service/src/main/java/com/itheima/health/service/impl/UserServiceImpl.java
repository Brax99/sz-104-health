package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.UserDao;
import com.itheima.health.pojo.User;
import com.itheima.health.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/29  18:05
 */
@Service(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Override
    public User findByUsername(String username) {
        //调用dao的方法通过id查询用户信息  用户下的角色 角色下的权限
        return userDao.findByUsername(username);
    }
}
