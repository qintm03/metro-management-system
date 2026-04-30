package com.example.springboot.mapper;

import com.example.springboot.entity.Zhandianzhoubian;

import java.util.List;

public interface ZhandianzhoubianMapper {

    List<Zhandianzhoubian> findAll();

    Zhandianzhoubian findById(Integer id);

    void insert(Zhandianzhoubian zhandianzhoubian);

    void updateById(Zhandianzhoubian zhandianzhoubian);

    void deleteById(Integer id);

    List<Zhandianzhoubian> findByPage(Integer offset, Integer pageSize);

    Integer count();

}
