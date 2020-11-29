package com.itheima.health.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.pojo.Permission;
import com.itheima.health.pojo.Role;
import com.itheima.health.pojo.User;
import com.itheima.health.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/29  17:53
 */
@Component
public class SpringSecurityUserService implements UserDetailsService {
    @Reference
    private UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //通过用户名查询用户信息（包括用户的角色  角色下的权限）
        User user = userService.findByUsername(username);
        //非空判断
        if (user != null) {
            //创建一个权限集合
            List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>();
            //获取用户下的角色信息
            Set<Role> userRoles = user.getRoles();
            //判断角色信息是否为空
            if (userRoles !=null) {
                userRoles.forEach(role -> {
                    //循环 添加角色  Keyword字段下的数据带ROLE_
                    authorityList.add(new SimpleGrantedAuthority(role.getKeyword()));
                    //角色下的权限 做个非空判断（角色不一定有权限）
                    Set<Permission> permissions = role.getPermissions();
                    if (permissions !=null) {
                        permissions.forEach(permission -> {
                            authorityList.add(new SimpleGrantedAuthority(permission.getKeyword()));
                        });
                    }
                });
            }
            //返回用户的权限集合信息
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(username,user.getPassword(),authorityList);
            return userDetails;

        }
        return null;
    }
}
