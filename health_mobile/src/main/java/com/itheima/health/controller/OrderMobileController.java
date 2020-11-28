package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.Exception.MyException;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Order;
import com.itheima.health.service.OrderService;
import com.itheima.health.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.Map;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/28  19:56
 */
@RestController
@RequestMapping("/order")
public class OrderMobileController {
    @Autowired
    private JedisPool jedisPool;

    @Reference
    private OrderService orderService;

    @PostMapping("/submit")
    public Result submit(@RequestBody Map<String,String> paraMap){
        // 验证码的校验
        Jedis jedis = jedisPool.getResource();
        //获取请求参数手机号码
        String telephone = paraMap.get("telephone");
        //拼接redis中的的key 形成完整的验证码  用于校验
        String key = RedisMessageConstant.SENDTYPE_ORDER+":"+telephone;
        // 从redis取出验证码
        String codeInRedis = jedis.get(key);
        //取出的验证码为空  用户没获取验证码或者已经过期
        if(StringUtils.isEmpty(codeInRedis)){
            //返回结果让用户重新获取验证码
            return new Result(false, "请重新获取验证码!");
        }
        //校验提交的验证码与从redis中查询的验证码是否一致
        if(!codeInRedis.equals(paraMap.get("validateCode"))) {
            //不一致 用户输入的验证码不正确 返回结果
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        //一直  删除redis中的key
        //为什么删除   防无聊的人在有效期内疯狂发送请求 重复提交数据 删除让这个只能成功校验一次
        jedis.del(key);
        jedis.close();
        // 设置预约类型 health_mobile微信预约
        paraMap.put("orderType", Order.ORDERTYPE_WEIXIN);
        // 验证码校验成功
        //- 通过日期查询预约设置表
        String orderDateString = paraMap.get("orderDate");
        //日期校验
        Date orderDate = null;
        try {
            // 在此处校验日期的格式
            orderDate = DateUtils.parseString2Date(orderDateString);
        } catch (Exception e) {
            //抛出异常 日期格式不正确
            throw new MyException("预约日期格式不正确");
        }
        //可以调用服务进行预约
        Integer id = orderService.submit(paraMap,orderDate);
        //返回添加结果
        return new Result(true, MessageConstant.ORDER_SUCCESS,id);
    }
    @GetMapping("/findById")
    public Result findById(int id){
        // 通过id调用服务查询信息
        Map<String,String> orderInfo = orderService.findOrderDetailById(id);
        //返回查询结果
        return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS,orderInfo);
    }
}
