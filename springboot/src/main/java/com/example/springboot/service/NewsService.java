package com.example.springboot.service;

import com.example.springboot.entity.News;
import com.example.springboot.mapper.NewsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {

    @Autowired
    private NewsMapper newsMapper;

    public int add(News news) {
        return newsMapper.insert(news);
    }

    public int update(News news) {
        return newsMapper.update(news);
    }

    public int delete(Integer id) {
        return newsMapper.deleteById(id);
    }

    public News findById(Integer id) {
        return newsMapper.selectById(id);
    }

    public List<News> findAll() {
        return newsMapper.selectAll();
    }

    public List<News> findByLimit(Integer limit) {
        return newsMapper.selectByLimit(limit);
    }

    public List<News> findByCondition(News news) {
        return newsMapper.selectByCondition(news);
    }

}
