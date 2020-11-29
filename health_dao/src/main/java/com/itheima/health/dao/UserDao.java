package com.itheima.health.dao;

import com.itheima.health.pojo.User;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/29  19:49
 */
public interface UserDao {
    //五表查询
    User findByUsername(String username);
}
