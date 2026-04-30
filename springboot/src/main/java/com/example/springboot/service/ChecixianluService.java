package com.example.springboot.service;

import com.example.springboot.entity.Checixianlu;
import com.example.springboot.mapper.ChecixianluMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChecixianluService {

    @Resource
    private ChecixianluMapper checixianluMapper;

    public List<Checixianlu> findAll() {
        return checixianluMapper.findAll();
    }

    public Checixianlu findById(Integer id) {
        return checixianluMapper.findById(id);
    }

    public void insert(Checixianlu checixianlu) {
        checixianluMapper.insert(checixianlu);
    }

    public void updateById(Checixianlu checixianlu) {
        checixianluMapper.updateById(checixianlu);
    }

    public void deleteById(Integer id) {
        checixianluMapper.deleteById(id);
    }

    public List<Checixianlu> findByPage(Integer offset, Integer pageSize) {
        return checixianluMapper.findByPage(offset, pageSize);
    }

    public Integer count() {
        return checixianluMapper.count();
    }

}
