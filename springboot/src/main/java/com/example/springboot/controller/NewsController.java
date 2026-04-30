package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.News;
import com.example.springboot.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @PostMapping
    public Result add(@RequestBody News news) {
        int result = newsService.add(news);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("发布失败");
    }

    @PutMapping
    public Result update(@RequestBody News news) {
        int result = newsService.update(news);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        int result = newsService.delete(id);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("删除失败");
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        News news = newsService.findById(id);
        return Result.success(news);
    }

    @GetMapping
    public Result findAll() {
        List<News> list = newsService.findAll();
        return Result.success(list);
    }

    @GetMapping("/limit/{limit}")
    public Result findByLimit(@PathVariable Integer limit) {
        List<News> list = newsService.findByLimit(limit);
        return Result.success(list);
    }

    @PostMapping("/search")
    public Result search(@RequestBody News news) {
        List<News> list = newsService.findByCondition(news);
        return Result.success(list);
    }

}
