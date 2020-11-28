package com.itheima.health.service;

import com.itheima.health.pojo.Member;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/28  22:34
 */
public interface MemberService {
    Member findByTelephone(String telephone);

    void add(Member member);
}
