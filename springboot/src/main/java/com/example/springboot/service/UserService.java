package com.example.springboot.service;

import org.springframework.stereotype.Service;


import com.example.springboot.entity.User;
import com.example.springboot.entity.Account;
import com.example.springboot.exception.CustomException;

import cn.hutool.core.util.ObjectUtil;

import jakarta.annotation.Resource;
import java.util.List;
import com.example.springboot.mapper.UserMapper;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public List<User> findAll() {
        List<User> list = userMapper.findAll();
        return list;
    }

    public void updataById(User user) {
        userMapper.updataById(user);
    }

    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }

    public void addUser(User user) {
        userMapper.addUser(user);

    }

    public List<User> findByPage(Integer offset, Integer pageSize) {
        return userMapper.findByPage(offset, pageSize);

    }

    public Integer count() {
        return userMapper.count();
    }

    public Account login(Account account) {
        User dbUser = userMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException("用户不存在");
        }
        if (!account.getPassword().equals(dbUser.getPassword())) {
            throw new CustomException("账号或密码错误");
        }
        return dbUser;

    }

}
