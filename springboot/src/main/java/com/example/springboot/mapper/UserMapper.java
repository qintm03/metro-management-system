package com.example.springboot.mapper;

import java.util.List;

import com.example.springboot.entity.User;

public interface UserMapper {

    List<User> findAll();

    void updataById(User user);

    void deleteById(Long id);

    void addUser(User user);

    List<User> findByPage(Integer offset, Integer pageSize);

    Integer count();

    User selectByUsername(String username);
}
