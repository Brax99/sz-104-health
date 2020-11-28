package com.itheima.health.dao;

import com.itheima.health.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/25  18:12
 */
public interface OrderSettingDao {
    OrderSetting findByOrderDate(Date orderDate);

    void update(OrderSetting orderSetting);

    void add(OrderSetting orderSetting);

    List<Map<String, Integer>> getDataByMonth(String month);

    int editReservationsByOrderDate(OrderSetting orderSetting);
}
