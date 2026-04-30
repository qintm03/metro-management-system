package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Config;
import com.example.springboot.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @PostMapping
    public Result add(@RequestBody Config config) {
        int result = configService.add(config);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("添加失败");
    }

    @PutMapping
    public Result update(@RequestBody Config config) {
        int result = configService.update(config);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        int result = configService.delete(id);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("删除失败");
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        Config config = configService.findById(id);
        return Result.success(config);
    }

    @GetMapping("/name/{name}")
    public Result findByName(@PathVariable String name) {
        Config config = configService.findByName(name);
        return Result.success(config);
    }

    @GetMapping
    public Result findAll() {
        List<Config> list = configService.findAll();
        return Result.success(list);
    }

}
