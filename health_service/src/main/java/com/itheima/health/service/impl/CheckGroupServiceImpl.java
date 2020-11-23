package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.Exception.MyException;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/23  10:31
 */
@Service(interfaceClass = CheckGroupService.class)
public class CheckGroupServiceImpl implements CheckGroupService {
    @Autowired
    private CheckGroupDao checkGroupDao;
    @Override
    @Transactional
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        checkGroupDao.add(checkGroup);
        Integer checkGroupId = checkGroup.getId();
        if(null != checkitemIds){
            for (Integer checkitemId : checkitemIds) {
                checkGroupDao.addCheckGroupCheckItem(checkGroupId, checkitemId);
            }
        }

    }

    @Override
    public PageResult<CheckGroup> findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())) {
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        Page<CheckGroup> checkGroupPageResult = checkGroupDao.findByCondition(queryPageBean.getQueryString());
        return new PageResult<CheckGroup>(checkGroupPageResult.getTotal(),checkGroupPageResult.getResult());
    }

    @Override
    public CheckGroup findById(int id) {
        return checkGroupDao.findById(id);
    }

    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(int id) {
        return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
    }

    @Override
    @Transactional
    public void update(CheckGroup checkGroup, Integer[] checkitemIds) {
        checkGroupDao.update(checkGroup);
        checkGroupDao.deleteCheckGroupCheckItem(checkGroup.getId());
        Integer checkGroupId = checkGroup.getId();
        if(null != checkitemIds){
            for (Integer checkitemId : checkitemIds) {
                checkGroupDao.addCheckGroupCheckItem(checkGroupId, checkitemId);
            }
        }
    }

    @Override
    public void deleteById(int id) {
        int count = checkGroupDao.findCountByCheckGroupId(id);
        if (count>0){
            throw new MyException("被套餐使用  无法删除");
        }
        checkGroupDao.deleteCheckGroupCheckItem(id);
        checkGroupDao.deleteById(id);
    }
}
