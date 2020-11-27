package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.QiNiuUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/27  18:48
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealMobileController {
    @Reference
    private SetmealService setmealService;

    @GetMapping("/getSetmeal")
    //查询套餐的信息
    public Result getSetmeal(){
        // 调用业务层方法查询套餐信息
        List<Setmeal> list = setmealService.findAll();
        // 前端回显图片 需要完整路径  用工具类查domain拼接
        list.forEach(setmeal->{
            setmeal.setImg(QiNiuUtils.DOMAIN + setmeal.getImg());
        });
        //返回查询结果
        return new Result(true, MessageConstant.QUERY_SETMEALLIST_SUCCESS,list);
    }
    //通过套餐id查询套餐的详情信息
    @GetMapping("/findDetailById")
    public Result findDetailById(int id){
        //调用业务层的方法查询套餐的详情信息(包括检查组和检查项的信息)
        Setmeal setmeal = setmealService.findDetailById(id);
        // 前端回显图片 需要完整路径  用工具类查domain拼接
        setmeal.setImg(QiNiuUtils.DOMAIN+setmeal.getImg());
        //返回查询的结果
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
    }
}
