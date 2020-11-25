package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderSettingService;
import com.itheima.health.utils.POIUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/25  17:19
 */
@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {
    @Reference
    private OrderSettingService orderSettingService;
    //批量添加预约信息
    @PostMapping("/upload")
    public Result upload(@RequestBody MultipartFile excelFile){
        try {
            //使用工具类方法解析上传的excel文件
            List<String[]> strings = POIUtils.readExcel(excelFile);
            //使用对象来封装数据 首先创一个新的ArrayList
            List<OrderSetting> orderSettingList = new ArrayList<OrderSetting>();
            //解析日期格式  工具类已经指定格式为"yyyy/MM/dd"
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(POIUtils.DATE_FORMAT);
            //循环遍历strings  为避免多次创建
            Date orderDate = null;
            OrderSetting os = null;
            for (String[] string : strings) {
                //第一行是日期的字符串
                orderDate = simpleDateFormat.parse(string[0]);
                //第二行是最大的预约数量  字符转成int
                int number = Integer.parseInt(string[1]);
                //构造对象 添加orderDate 和 number(对不起忘了lambda表达式  只能这样转换)
                os = new OrderSetting(orderDate,number);
                //添加数据到List
                orderSettingList.add(os);
            }
            //转换完成  调用业务层方法添加数据
            orderSettingService.add(orderSettingList);
            //返回添加信息
            return new Result(true, MessageConstant.IMPORT_ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);
        }
    }
}
