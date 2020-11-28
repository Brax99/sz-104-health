package com.itheima.health.service;

import com.itheima.health.Exception.MyException;

import java.util.Date;
import java.util.Map;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/28  20:06
 */
public interface OrderService {
    Integer submit(Map<String, String> paraMap, Date orderDate)throws MyException;

    Map<String, String> findOrderDetailById(int id);
}
