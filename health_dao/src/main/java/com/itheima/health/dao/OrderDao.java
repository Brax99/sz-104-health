package com.itheima.health.dao;


import com.itheima.health.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrderDao {
    /**
     * 添加
     * @param order
     */
    void add(Order order);

    /**
     * 条件查询
     * @param order
     * @return
     */
    List<Order> findByCondition(Order order);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    Map findById4Detail(Integer id);

    // 报表相关
    Integer findOrderCountByDate(String date);
    Integer findOrderCountAfterDate(String date);
    Integer findVisitsCountByDate(String date);
    Integer findVisitsCountAfterDate(String date);
    //在日期范围内查询数据
    Integer findOrderCountBetweenDate(@Param("startDate") String startDate, @Param("endDate") String endDate);
    List<Map> findHotSetmeal();

}
