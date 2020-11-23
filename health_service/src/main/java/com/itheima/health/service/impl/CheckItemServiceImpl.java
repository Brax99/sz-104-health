package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.Exception.MyException;
import com.itheima.health.dao.CheckItemDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/21  17:37
 */
@Service(interfaceClass = CheckItemService.class)
public class CheckItemServiceImpl implements CheckItemService {
    @Autowired
    private CheckItemDao checkItemDao;
    @Override
    public List<CheckItem> findAll() {
        //查询所有（不分页）
        return checkItemDao.findAll();
    }

    @Override
    public void add(CheckItem checkItem) {
        //添加检查组
        checkItemDao.add(checkItem);
    }

    @Override
    public CheckItem findById(int id) {
        //通过id查检查项
        return checkItemDao.findById(id);
    }

    @Override
    public PageResult<CheckItem> findPage(QueryPageBean queryPageBean) {
        //Mapper接口方式的调用 让其分页查询
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        //判断查询条件是否为空 不为空则拼接百分号
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())){
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        //查询所有检查项结果集  如果有查询条件就进行模糊查询
        Page<CheckItem> page = checkItemDao.findByCondition(queryPageBean.getQueryString());
        //把查询到的total和结果集封装到PageResult中
        long total = page.getTotal();
        List<CheckItem> rows = page.getResult();
        return new PageResult<CheckItem>(total,rows);
    }

    @Override
    public void update(CheckItem checkItem) {
        //修改检查项
        checkItemDao.update(checkItem);
    }

    @Override
    public void deleteById(int id)throws MyException{
        //判断是否被检查组使用
        int count = checkItemDao.findCountByCheckItemId(id);
        if (count>0){
            //如果被使用则不能删除 抛出异常
             throw  new MyException("被检查组使用，无法删除");
        }
        //没有被使用则通过id删除
        checkItemDao.deleteById(id);
    }

}
