package com.itheima.health.dao;

import com.itheima.health.pojo.OrderSetting;

import java.util.Date;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/25  18:12
 */
public interface OrderSettingDao {
    OrderSetting findByOrderDate(Date orderDate);

    void update(OrderSetting orderSetting);

    void add(OrderSetting orderSetting);
}
