package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.Exception.MyException;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.dao.OrderDao;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.pojo.Member;
import com.itheima.health.pojo.Order;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderService;
import com.itheima.health.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/28  20:07
 */
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderSettingDao orderSettingDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private OrderDao orderDao;

    @Override
    @Transactional
    public Integer submit(Map<String, String> paraMap,Date orderDate) {
        //通过预约日期查询预约设置表
        OrderSetting orderSetting = orderSettingDao.findByOrderDate(orderDate);
        //判断是否存在该数据
        if(null == orderSetting) {
            //没有该数据  说明当天没准备预约业务
            throw new MyException("所选日期没有预约业务，请择日再来");
        }
        // 有记录可以预约  但是还需判断已预约人数是否大于等于最大预约数
        if(orderSetting.getReservations() >= orderSetting.getNumber()) {
            // 判断已预约人数大于等于最大预约数   说明没预约名额
            throw new MyException("所选日期预约已满，请下次赶早");
        }
        // 构建重复预约查询条件
        Order order = new Order();
        //获取   设置套餐id
        order.setSetmealId(Integer.valueOf(paraMap.get("setmealId")));
        //获取   设置预约日期
        order.setOrderDate(orderDate);
        // 获取手机号码
        String telephone = paraMap.get("telephone");
        //通过手机号码查询该用户是否为会员
        Member member = memberDao.findByTelephone(telephone);
        // 为空说明还未注册成为会员
        if(null == member){
            //为其注册会员
            member = new Member();
            member.setPhoneNumber(telephone);
            member.setRegTime(new Date());
            member.setRemark("微信预约注册");
            String idCardNo = paraMap.get("idCard");
            member.setPassword(idCardNo.substring(idCardNo.length()-6));
            member.setIdCard(idCardNo);
            member.setSex(paraMap.get("sex"));
            member.setName(paraMap.get("name"));
            //添加这个会员
            memberDao.add(member);
            order.setMemberId(member.getId());
            //不为空说明已经是会员
        }else{
            // 获取会员id
            Integer memberId = member.getId();
            //判断其是否重复预约当天的该套餐业务
            order.setMemberId(memberId);
            //通过预约日期，会员id,套餐id查询t_order 是否存在该数据
            List<Order> orderList = orderDao.findByCondition(order);
            // 存在就说明已经预约过当天的业务
            if(null != orderList && orderList.size() > 0){
                //报错 抛出异常信息
                throw new MyException("已经预约过该业务，请忽重复预约");
            }
        }
        // 更新已预约人数
        int count = orderSettingDao.editReservationsByOrderDate(orderSetting);
        //并发，加分布式锁（利用数据库的行锁） where reservations>number
        // 如果更新失败，则返回0，报错，预约已满，请选择其它日期
        if(count == 0){
            throw new MyException("预约已满，请选择其它日期");
        }
        //如果更新成功  mybatis则会返回1
        //添加订单信息  返回主键
        order.setOrderStatus(Order.ORDERSTATUS_NO);
        order.setOrderType(paraMap.get("orderType"));
        orderDao.add(order);
        //返回订单的id给controller
        return order.getId();
    }

    @Override
    public Map<String, Object> findOrderDetailById(int id) {
        return orderDao.findById4Detail(id);
    }

}
