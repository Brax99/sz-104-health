package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyuncs.exceptions.ClientException;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.service.OrderService;
import com.itheima.health.utils.SMSUtils;
import com.itheima.health.utils.ValidateCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/28  18:23
 */
@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {

    private static final Logger log = LoggerFactory.getLogger(ValidateCodeController.class);
    @Autowired
    private JedisPool jedisPool;
    @Reference
    private OrderService orderService;

    @PostMapping("/send4Order")
    public Result send4Order(String telephone) {
        //获取连接池的一个redis对象
        Jedis jedis = jedisPool.getResource();
        //生成redis 中的key  带上业务表示明确目的
        //SENDTYPE_GETPWD ="003";用于缓存找回密码时发送的验证码
        String  key = RedisMessageConstant.SENDTYPE_ORDER +":"+telephone;
        //获取redis中的验证码
        String codeInRedis = jedis.get(key);
        //重要信息  debug记录一下
        log.debug("Redis中的验证码: {},{}",codeInRedis,telephone);
        //判断查询到的验证码是否为空
        if(!StringUtils.isEmpty(codeInRedis)){
            //不为空 说明已经发送过且还未失效
            return new Result(false, "验证码已经发送过了，请注意查收!");
        }
        //为空说明没有发送过或已过期，则使用工具类生成验证码
        String code = String.valueOf(ValidateCodeUtils.generateValidateCode(6));
        //使用短信服务工具类发送验证码
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone, code);
        } catch (ClientException e) {
            log.error("发送验证码失败",e);
            //来到这里发送失败  返回发送结果
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        log.debug("验证码发送成功: {},{}", code, telephone);
        //验证码存入redis，同时要设置有效期，一般为5-15
        //防止用户忘记验证码 定时就删除已经废用的验证码 使其不再占用内存空间
        jedis.setex(key, 10*60, code);
        jedis.close();
        //返回发送结果
        return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

    @PostMapping("/send4Login")
    public Result send4Login(String telephone){
        //获取连接池的一个redis对象
        Jedis jedis = jedisPool.getResource();
        //生成redis 中的key  带上业务表示明确目的
        //SENDTYPE_LOGIN = "002";//用于缓存手机号快速登录时发送的验证码
        String key = RedisMessageConstant.SENDTYPE_LOGIN + ":" + telephone;
        //获取redis中的验证码
        String codeInRedis = jedis.get(key);
        //重要信息  debug记录一下
        log.debug("Redis中的验证码: {},{}",codeInRedis,telephone);
        //判断查询到的验证码是否为空
        if(!StringUtils.isEmpty(codeInRedis)){
            //不为空 说明已经发送过且还未失效
            return new Result(false, "验证码已经发送过了，请注意查收!");
        }
        //为空说明没有发送过或已过期，则使用工具类生成验证码
        String code = String.valueOf(ValidateCodeUtils.generateValidateCode(6));
        //使用短信服务工具类发送验证码
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone, code);
        } catch (ClientException e) {
            log.error("发送验证码失败",e);
            //来到这里发送失败  返回发送结果
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        log.debug("验证码发送成功: {},{}", code, telephone);
        //验证码存入redis，同时要设置有效期，一般为5-15
        //防止用户忘记验证码 定时就删除已经废用的验证码 使其不再占用内存空间
        jedis.setex(key, 10*60, code);
        jedis.close();
        //返回发送结果
        return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

}
