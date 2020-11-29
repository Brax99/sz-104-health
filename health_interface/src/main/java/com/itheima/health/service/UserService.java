package com.itheima.health.service;

import com.itheima.health.pojo.User;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/29  18:03
 */
public interface UserService {
    User findByUsername(String username);
}
