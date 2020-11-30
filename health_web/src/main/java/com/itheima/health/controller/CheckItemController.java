package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/21  17:28
 */
@RestController
@RequestMapping("/checkitem")
public class CheckItemController {
    @Reference
    private CheckItemService checkItemService;
    @GetMapping("/findAll")
    public Result findAll(){
        //调用业务层方法查询所有结果集 封装到集合
        List<CheckItem> list = checkItemService.findAll();
        //将查询到的集合封装到Result 响应给前端
        return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,list);
    }
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('CHECKITEM_ADD')")
    public Result add(@RequestBody CheckItem checkItem){
        //调用Service的add方法添加
        checkItemService.add(checkItem);
        //返回添加信息
        return new Result(true,MessageConstant.ADD_CHECKGROUP_SUCCESS);
    }
    @GetMapping("/findById")
    public Result findById(int id){
        //通过id查询CheckItem
        CheckItem checkItem = checkItemService.findById(id);
        //返回结果  把信息和CheckItem对象封装到Result对象
        return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItem);
    }
    @PostMapping("/findPage")
    public Result findPage(@RequestBody QueryPageBean queryPageBean){
        //分页查询 调用service的方法  传入当前页数和页数大小(每页条数)以及可能会有的查询条件
        PageResult<CheckItem> pageResult = checkItemService.findPage(queryPageBean);
        //把查询到的结果集合和total查询信息响应
        return new Result(true,MessageConstant.QUERY_CHECKITEM_SUCCESS,pageResult);
    }
    @PostMapping("/update")
    public Result update(@RequestBody CheckItem checkItem){
        //调用业务层的方法 携带参数是CheckItem对象
        checkItemService.update(checkItem);
        //返回查询信息
        return new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS);
    }
    @PostMapping("/deleteById")
    public Result deleteById(int id){
        //通过id删除检查项
        checkItemService.deleteById(id);
        //返回删除信息
        return new Result(true,MessageConstant.DELETE_CHECKITEM_SUCCESS);
    }
}
