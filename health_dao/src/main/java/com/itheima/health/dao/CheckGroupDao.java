package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.entity.PageResult;
import com.itheima.health.pojo.CheckGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/23  10:33
 */
public interface CheckGroupDao {
    void add(CheckGroup checkGroup);

    //参数有两个 类型都为int  需要加@Param注释取别名  不然分不清传入的参数
    void addCheckGroupCheckItem(@Param("checkGroupId") Integer checkGroupId, @Param("checkitemId")Integer checkitemId);

    Page<CheckGroup> findByCondition(String queryString);

    CheckGroup findById(int id);

    List<Integer> findCheckItemIdsByCheckGroupId(int id);

    void update(CheckGroup checkGroup);

    void deleteCheckGroupCheckItem(Integer id);

    int findCountByCheckGroupId(int id);

    void deleteById(int id);
}
