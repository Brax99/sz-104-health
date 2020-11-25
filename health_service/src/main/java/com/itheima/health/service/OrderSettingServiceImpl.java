package com.itheima.health.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.Exception.MyException;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.pojo.OrderSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/25  17:51
 */
@Service(interfaceClass = OrderSettingService.class)
public class OrderSettingServiceImpl implements OrderSettingService {
    @Autowired
    private OrderSettingDao orderSettingDao;
    //添加预约设置
    @Override
    @Transactional
    public void add(List<OrderSetting> orderSettingList)throws MyException {
        //首先遍历传过来的orderSettingList
        //严谨就完事了  加个非空判断 添加个事务控制
        if (orderSettingList !=null){
            for (OrderSetting orderSetting : orderSettingList) {
                //拿到日期
                Date orderDate = orderSetting.getOrderDate();
                //通过日期判断数据库是否有该数据（为什么用日期  因为日期不可重复）
                OrderSetting order= orderSettingDao.findByOrderDate(orderDate);
                //判断查询的数据是否为空  不为空就是有该数据
                if (order !=null) {
                    //如果有就做修改信息操作  修改要判断时预约最大人数是否小于已预约人数
                    if (orderSetting.getNumber()<order.getReservations()){
                        //小于  要报错 声明异常（这里声明了接口也要声明）
                        throw new MyException("最大预约人数小于已预约人数 不符合要求");
                    }
                    //来到这  说明最大预约人数大于已预约人数 可以进行修改
                    orderSettingDao.update(orderSetting);
                }else {
                    //数据库没有该数据 进行添加操作
                    orderSettingDao.add(orderSetting);
                }
            }
        }
    }
}
