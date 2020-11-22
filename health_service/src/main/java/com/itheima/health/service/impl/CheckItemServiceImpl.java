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
        return checkItemDao.findAll();
    }

    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    @Override
    public CheckItem findById(int id) {
        return checkItemDao.findById(id);
    }

    @Override
    public PageResult<CheckItem> findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        //panduan
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())){
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        Page<CheckItem> page = checkItemDao.findByCondition(queryPageBean.getQueryString());
        long total = page.getTotal();
        List<CheckItem> rows = page.getResult();
        return new PageResult<CheckItem>(total,rows);
    }

    @Override
    public void update(CheckItem checkItem) {
        checkItemDao.update(checkItem);
    }

    @Override
    public void deleteById(int id){
        int count = checkItemDao.findCountByCheckItemId(id);
        if (count>0){
             throw  new MyException("被检查组使用，无法删除");
        }
        checkItemDao.deleteById(id);
    }

}
