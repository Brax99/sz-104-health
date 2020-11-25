package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.pojo.Setmeal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/24  17:08
 */
public interface SetmealDao {
    void add(Setmeal setmeal);

    //两个相同类型参数 需要取别名
    void addSetmealCheckGroup(@Param("setmealId")Integer setmealId, @Param("checkgroupId")Integer checkgroupId);

    Page<Setmeal> findByCondition(String queryString);

    Setmeal findById(int id);

    List<Integer> findCheckgroupIdsBySetmealId(int id);

    void update(Setmeal setmeal);

    void deleteSetmealCheckGroup(Integer id);

    int findOrderCountBySetmealId(int id);

    void deleteById(int id);
}