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
        //首先添加查询组信息CheckGroup对象
        checkGroupDao.add(checkGroup);
        // 获取检查组的id
        Integer checkGroupId = checkGroup.getId();
        //判断检查项id数组是否为空
        if(null != checkitemIds){
            for (Integer checkitemId : checkitemIds) {
                //不为空 添加检查组和检查项的关系
                checkGroupDao.addCheckGroupCheckItem(checkGroupId, checkitemId);
            }
        }

    }

    @Override
    public PageResult<CheckGroup> findPage(QueryPageBean queryPageBean) {
        //Mapper接口方式的调用 让其帮我我们进行分页查询
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        //判断是否有查询条件 有则给查询条件添加上%%
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())) {
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        //如果有查询条件就模糊查询  没有就查询所有
        Page<CheckGroup> checkGroupPageResult = checkGroupDao.findByCondition(queryPageBean.getQueryString());
        //获取查询到的total和结果集  封装到PageResult中
        return new PageResult<CheckGroup>(checkGroupPageResult.getTotal(),checkGroupPageResult.getResult());
    }

    @Override
    public CheckGroup findById(int id) {
        //通过id查询检查组
        return checkGroupDao.findById(id);
    }

    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(int id) {
        //通过id查询选中的检查项集合
        return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
    }

    @Override
    @Transactional
    public void update(CheckGroup checkGroup, Integer[] checkitemIds) {
        //首先修改检查组信息
        checkGroupDao.update(checkGroup);
        //删除原来的检查组和检查项的关系
        checkGroupDao.deleteCheckGroupCheckItem(checkGroup.getId());
        //拿到检查组的id
        Integer checkGroupId = checkGroup.getId();
        //如果选中的检查项id不为空 则添加检查组和检查项的关系
        if(null != checkitemIds){
            for (Integer checkitemId : checkitemIds) {
                checkGroupDao.addCheckGroupCheckItem(checkGroupId, checkitemId);
            }
        }
    }

    @Override
    public void deleteById(int id)throws MyException {
        //判断是否被套餐使用
        int count = checkGroupDao.findCountByCheckGroupId(id);
        if (count>0){
            //被套餐使用则报错 抛出异常
            throw new MyException("被套餐使用  无法删除");
        }
        //删除检查组和检查项的关系
        checkGroupDao.deleteCheckGroupCheckItem(id);
        //删除检查组信息
        checkGroupDao.deleteById(id);
    }
}
