package com.example.springboot.service;

import org.springframework.stereotype.Service;
import com.example.springboot.entity.Account;


import com.example.springboot.mapper.AdminMapper;

import com.example.springboot.exception.CustomException;

import jakarta.annotation.Resource;
import cn.hutool.core.util.ObjectUtil;

@Service
public class AdminService {
    @Resource
    private AdminMapper adminMapper;

  public Account login(Account account) {
        Account dbAdmin = adminMapper.selectByUsername(account.getUsername());//根据用户名查询数据库中的用户信息，最后返回了数据库中这条信息
        if (ObjectUtil.isNull(dbAdmin)) {  /* 如果根据用户名没有在数据库查到该用户，会返回
                                            空值包装在dbAdmin对象中，此时执行if判断逻辑，如果为空则抛出用户不存在。 */
            
            throw new CustomException("用户不存在"); //抛出自定义异常，给CustomException类中的msg对象赋值
        }
        if (!account.getPassword().equals(dbAdmin.getPassword())) {
            throw new CustomException("账号或密码错误");
        }
        return dbAdmin;

    }
}
