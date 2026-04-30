package com.example.springboot.service;

import com.example.springboot.entity.Zhandianzhoubian;
import com.example.springboot.mapper.ZhandianzhoubianMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZhandianzhoubianService {

    @Resource
    private ZhandianzhoubianMapper zhandianzhoubianMapper;

    public List<Zhandianzhoubian> findAll() {
        return zhandianzhoubianMapper.findAll();
    }

    public Zhandianzhoubian findById(Integer id) {
        return zhandianzhoubianMapper.findById(id);
    }

    public void insert(Zhandianzhoubian zhandianzhoubian) {
        zhandianzhoubianMapper.insert(zhandianzhoubian);
    }

    public void updateById(Zhandianzhoubian zhandianzhoubian) {
        zhandianzhoubianMapper.updateById(zhandianzhoubian);
    }

    public void deleteById(Integer id) {
        zhandianzhoubianMapper.deleteById(id);
    }

    public List<Zhandianzhoubian> findByPage(Integer offset, Integer pageSize) {
        return zhandianzhoubianMapper.findByPage(offset, pageSize);
    }

    public Integer count() {
        return zhandianzhoubianMapper.count();
    }

}
