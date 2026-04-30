package com.example.springboot.mapper;

import com.example.springboot.entity.Zhandianchaxun;

import java.util.List;

public interface ZhandianchaxunMapper {

    List<Zhandianchaxun> findAll();

    Zhandianchaxun findById(Integer id);

    void insert(Zhandianchaxun zhandianchaxun);

    void updateById(Zhandianchaxun zhandianchaxun);

    void deleteById(Integer id);

    List<Zhandianchaxun> findByPage(Integer offset, Integer pageSize);

    Integer count();

}
