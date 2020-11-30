package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.service.MemberService;
import com.itheima.health.service.SetmealService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/30  15:03
 */
@RestController
@RequestMapping("/report")
public class ReportController {
    @Reference
    private MemberService memberService;
    @Reference
    private SetmealService setmealService;
    //查询月份集合还有当月的会员数量集合 折线图
    @GetMapping("/getMemberReport")
    public Result getMemberReport(){
        //创建集合用来装月份
        List<String> months = new ArrayList<String>();
        //获取日历对象
        Calendar calendar = Calendar.getInstance();
        //获取年份然后减一  得到去年的
        calendar.add(Calendar.YEAR,-1);
        //设置日期格式  要（2019-10）这样的数据格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        //循环遍历12次  每次加一个月 获取去年到今年的月份加入集合
        for (int i = 0; i < 12; i++) {
            //加一个月 然后加入月份集合
            calendar.add(Calendar.MONTH,1);
            //获取日期  修改格式  加入集合
            months.add(simpleDateFormat.format(calendar.getTime()));
        }
        //调用业务层的方法  传入数据集合 查出截止到当前月的会员数量集合
        List<Integer> memberCount = memberService.getMemberReport(months);
        //把月份集合还有会员数量集合放入Map中
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("months",months);
        resultMap.put("memberCount",memberCount);
        //封装到result对象返回
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_FAIL,resultMap);
    }
    //套餐数量饼图
    @GetMapping("/getSetmealReport")
    public Result getSetmealReport(){
        //调用业务层代码查询套餐预约占比
        //需要的数据类型是一个map{value ，name}
        List<Map<String, Object>> list = setmealService.findSetmealCount();
        //遍历list  取出map中的name  将其转换为String 然后存入setmealNames集合中
        //实现方式1  stream流 用map转
        //List<String> setmealNames = list.stream().map(m->(String)m.get("name")).collect(Collectors.toList());
        //实现方式2 循环
        //创建一个setmealNames集合（套餐名集合）
        List<String> setmealNames = new ArrayList<String>();
        for (Map<String, Object> map : list) {
            //取出数据 转换为String
           String setmealName =(String) map.get("name");
           //加入数据到name集合
           setmealNames.add(setmealName);
        }
        //前端需要的数据
        // Map<String,Object>;
        //map.put(“setmealNames”,List<String>);
        //map.put(“setmealCount”,List<Map>)
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("setmealNames",setmealNames);
        resultMap.put("setmealCount",list);
        //返回查询结果
        return new Result(true,MessageConstant.GET_SETMEAL_COUNT_REPORT_FAIL,resultMap);
    }
}
