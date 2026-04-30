package com.example.springboot.service;

import com.example.springboot.entity.Config;
import com.example.springboot.mapper.ConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigService {

    @Autowired
    private ConfigMapper configMapper;

    public int add(Config config) {
        return configMapper.insert(config);
    }

    public int update(Config config) {
        return configMapper.update(config);
    }

    public int delete(Integer id) {
        return configMapper.deleteById(id);
    }

    public Config findById(Integer id) {
        return configMapper.selectById(id);
    }

    public Config findByName(String name) {
        return configMapper.selectByName(name);
    }

    public List<Config> findAll() {
        return configMapper.selectAll();
    }

}
