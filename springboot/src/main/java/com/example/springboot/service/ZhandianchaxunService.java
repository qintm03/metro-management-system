package com.example.springboot.service;

import com.example.springboot.entity.Zhandianchaxun;
import com.example.springboot.mapper.ZhandianchaxunMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZhandianchaxunService {

    @Resource
    private ZhandianchaxunMapper zhandianchaxunMapper;

    public List<Zhandianchaxun> findAll() {
        return zhandianchaxunMapper.findAll();
    }

    public Zhandianchaxun findById(Integer id) {
        return zhandianchaxunMapper.findById(id);
    }

    public void insert(Zhandianchaxun zhandianchaxun) {
        zhandianchaxunMapper.insert(zhandianchaxun);
    }

    public void updateById(Zhandianchaxun zhandianchaxun) {
        zhandianchaxunMapper.updateById(zhandianchaxun);
    }

    public void deleteById(Integer id) {
        zhandianchaxunMapper.deleteById(id);
    }

    public List<Zhandianchaxun> findByPage(Integer offset, Integer pageSize) {
        return zhandianchaxunMapper.findByPage(offset, pageSize);
    }

    public Integer count() {
        return zhandianchaxunMapper.count();
    }

}
