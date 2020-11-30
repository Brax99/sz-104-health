package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.Exception.MyException;
import com.itheima.health.dao.SetmealDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/24  17:05
 */
@Service(interfaceClass = SetmealService.class)
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealDao setmealDao;
    @Override
    @Transactional
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        //添加套餐信息
        setmealDao.add(setmeal);
        //获取套餐的id
        Integer setmealId = setmeal.getId();
        // 非空判断遍历检查组的id
        if(null != checkgroupIds) {
            for (Integer checkgroupId : checkgroupIds) {
                // 添加套餐与检查组的关系
                setmealDao.addSetmealCheckGroup(setmealId, checkgroupId);
            }
        }
    }

    @Override
    public PageResult<Setmeal> findPage(QueryPageBean queryPageBean) {
        //调用工具帮我们进行分页
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        //判断是否有判断条件
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())) {
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        //通过条件查询套餐信息用Page<>装
        Page<Setmeal> setmealPage = setmealDao.findByCondition(queryPageBean.getQueryString());
        //获取total和Result结果集返回
        return new PageResult<Setmeal>(setmealPage.getTotal(),setmealPage.getResult());
    }


    @Override
    public Setmeal findById(int id) {
        //通过选中的id查询Setmeal对象
        return setmealDao.findById(id);
    }

    @Override
    public List<Integer> findCheckgroupIdsBySetmealId(int id) {
        //通过套餐id查询所关联的检查组id信息
        return setmealDao.findCheckgroupIdsBySetmealId(id);
    }

    @Override
    @Transactional
    public void update(Setmeal setmeal, Integer[] checkgroupIds) {
        //首先更新套餐信息
        setmealDao.update(setmeal);
        //删除原来的套餐和检查组关系
        setmealDao.deleteSetmealCheckGroup(setmeal.getId());
        //添加新的检查组和套餐的关系（添加写过直接用）
        //获取套餐的id
        Integer setmealId = setmeal.getId();
        // 非空判断遍历检查组的id
        if(null != checkgroupIds) {
            for (Integer checkgroupId : checkgroupIds) {
                // 添加套餐与检查组的关系
                setmealDao.addSetmealCheckGroup(setmealId, checkgroupId);
            }
        }
    }

    @Override
    @Transactional
    public void deleteById(int id)throws MyException {
        //判断套餐是否被订单关联（调用dao的方法查询）
        int count = setmealDao.findOrderCountBySetmealId(id);
        //如果被关联  抛出异常信息
        if (count>0) {
            throw new MyException("该套餐已经被订单关联  无法删除");
        }
        //如果没被关联  就通过套餐id删除套餐信息
        //首先删除套餐和检查组的关系
        setmealDao.deleteSetmealCheckGroup(id);
        //再删除套餐信息
        setmealDao.deleteById(id);
    }

    @Override
    public List<String> findImgs() {
        //找图片
        return setmealDao.findImgs();
    }

    @Override
    public List<Setmeal> findAll() {
        //查询所有套餐信息
        return setmealDao.findAll();
    }

    @Override
    public Setmeal findDetailById(int id) {
        //通过id查询套餐的详情信息（包括里面的检查组和检查项信息）
        return setmealDao.findDetailById(id);
    }

    @Override
    public List<Map<String, Object>> findSetmealCount() {
        //查询套餐统计数据
        return setmealDao.findSetmealCount();
    }
}
