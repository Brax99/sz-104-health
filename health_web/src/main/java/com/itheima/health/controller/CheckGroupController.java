package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.service.CheckGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/23  10:26
 */
@RestController
@RequestMapping("/checkgroup")
public class CheckGroupController {
    //导阿里巴巴的包
    @Reference
    private CheckGroupService checkGroupService;
    @PostMapping("/add")
    public Result add(@RequestBody CheckGroup checkGroup,Integer[] checkitemIds){
        //接收参数 CheckGroup对象和用户勾选的检查项id（用数组封装）调用业务层的方法
        checkGroupService.add(checkGroup, checkitemIds);
        //返回添加结果
        return new Result(true, MessageConstant.ADD_CHECKGROUP_SUCCESS);
    }
    @PostMapping("/findPage")
    public Result findPage(@RequestBody QueryPageBean queryPageBean){
        //接收请求参数 当前页数 每页大小（每页行数）和可能有的查询条件
        PageResult<CheckGroup> pageResult = checkGroupService.findPage(queryPageBean);
        //返回查询pageResult（结果集和total）和查询结果封装到result中
        return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,pageResult);
    }
    @GetMapping("/findById")
    public Result findById(int id){
        //通过id查询检查组 调用业务层方法
        CheckGroup checkGroup = checkGroupService.findById(id);
        //返回查询结果和查询到的CheckGroup对象
        return new Result(true,MessageConstant.QUERY_CHECKITEM_SUCCESS,checkGroup);
    }
    @GetMapping("/findCheckItemIdsByCheckGroupId")
    public Result findCheckItemIdsByCheckGroupId(int id){
        //通过查询组id查询所选中的查询项id  用List装
        List<Integer> checkItemIds = checkGroupService.findCheckItemIdsByCheckGroupId(id);
        //返回查询信息和检查项id数组
        return new Result(true,MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItemIds);
    }
    @PostMapping("/update")
    public Result update(@RequestBody CheckGroup checkGroup,Integer[] checkitemIds){
        //接受请求参数 CheckGroup对象和检查项id数组
        checkGroupService.update(checkGroup,checkitemIds);
        //返回修改结果
        return new Result(true,MessageConstant.EDIT_CHECKGROUP_SUCCESS);
    }
    @PostMapping("/deleteById")
    public Result deleteById(int id){
        //通过检查组id删除检查组
        checkGroupService.deleteById(id);
        //返回删除信息
        return new Result(true,MessageConstant.DELETE_CHECKGROUP_SUCCESS);
    }
    @GetMapping("/findAll")
    public Result findAll(){
        List<CheckGroup> all = checkGroupService.findAll();
        return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,all);
    }
}
