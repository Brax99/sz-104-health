package com.itheima.health.service;

import com.itheima.health.Exception.MyException;
import com.itheima.health.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/25  17:50
 */
public interface OrderSettingService {
    void add(List<OrderSetting> orderSettingList);

    List<Map<String, Integer>> getDataByMonth(String month);

    void editNumberByDate(OrderSetting orderSetting)throws MyException;
}
