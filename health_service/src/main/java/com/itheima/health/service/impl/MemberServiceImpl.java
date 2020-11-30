package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.pojo.Member;
import com.itheima.health.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/28  22:35
 */
@Service(interfaceClass = MemberService.class)
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberDao memberDao;
    @Override
    //查询是否为会员
    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    @Override
    //添加会员
    public void add(Member member) {
        memberDao.add(member);
    }

    //通过月份查询会员数量
    @Override
    public List<Integer> getMemberReport(List<String> months) {
        //创建一个集合用来封装会员数量数据
        List<Integer> list = new ArrayList<>();
        //遍历月份集合
        months.forEach(month->{
            //拼接上"-31" 查询当当月月底为止的会员总数
            list.add(memberDao.findMemberCountBeforeDate(month+"-31"));
        });
        return list;
    }
}
