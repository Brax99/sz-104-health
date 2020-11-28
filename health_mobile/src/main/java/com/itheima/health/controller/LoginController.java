package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Member;
import com.itheima.health.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/28  22:28
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private MemberService memberService;

    @PostMapping("/check")
    public Result check(@RequestBody Map<String,String> loginInfo, HttpServletResponse res){
        // 验证码的校验
        Jedis jedis = jedisPool.getResource();
        //获取请求参数手机号码
        String telephone = loginInfo.get("telephone");
        //拼接redis中的的key 形成完整的验证码  用于校验
        String key = RedisMessageConstant.SENDTYPE_LOGIN+":"+telephone;
        // 从redis取出验证码
        String codeInRedis = jedis.get(key);
        //取出的验证码为空  用户没获取验证码或者已经过期
        if(StringUtils.isEmpty(codeInRedis)){
            //返回结果让用户重新获取验证码
            return new Result(false, "请重新获取验证码!");
        }
        //校验提交的验证码与从redis中查询的验证码是否一致
        if(!codeInRedis.equals(loginInfo.get("validateCode"))) {
            //不一致 用户输入的验证码不正确 返回结果
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        //一直  删除redis中的key
        //为什么删除   防无聊的人在有效期内疯狂发送请求 重复提交数据 删除让这个只能成功校验一次
        jedis.del(key);
        jedis.close();
        // 判断是否为会员
        Member member = memberService.findByTelephone(telephone);
        //非会员就添加为会员
        if(null == member){
            member = new Member();
            member.setRemark("手机快速登陆");
            member.setRegTime(new Date());
            member.setPhoneNumber(telephone);
            memberService.add(member);
        }
        //使用cookie保存用户的登录信息
        Cookie cookie = new Cookie("login_member_telephone", telephone);
        // 时效30天
        cookie.setMaxAge(30*24*60*60);
        //访问的路径根路径下时网站的所有路径都会发送这个cookie
        cookie.setPath("/");
        res.addCookie(cookie);
        return new Result(true, MessageConstant.LOGIN_SUCCESS);
    }
}
