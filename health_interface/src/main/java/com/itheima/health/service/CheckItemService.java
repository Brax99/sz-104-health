package com.itheima.health.service;

import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.pojo.CheckItem;

import java.util.List;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/21  17:35
 */
public interface CheckItemService {
    List<CheckItem> findAll();

    void add(CheckItem checkItem);

    CheckItem findById(int id);

    void update(CheckItem checkItem);
}
