package com.example.springboot.mapper;

import com.example.springboot.entity.Checixianlu;

import java.util.List;

public interface ChecixianluMapper {

    List<Checixianlu> findAll();

    Checixianlu findById(Integer id);

    void insert(Checixianlu checixianlu);

    void updateById(Checixianlu checixianlu);

    void deleteById(Integer id);

    List<Checixianlu> findByPage(Integer offset, Integer pageSize);

    Integer count();

}
