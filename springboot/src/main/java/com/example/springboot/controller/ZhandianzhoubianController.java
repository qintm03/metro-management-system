package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Page;
import com.example.springboot.entity.Zhandianzhoubian;
import com.example.springboot.service.ZhandianzhoubianService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/zhandianzhoubian")
public class ZhandianzhoubianController {

    @Resource
    private ZhandianzhoubianService zhandianzhoubianService;

    @GetMapping
    public Result findAll() {
        List<Zhandianzhoubian> list = zhandianzhoubianService.findAll();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable("id") Integer id) {
        Zhandianzhoubian zhandianzhoubian = zhandianzhoubianService.findById(id);
        return Result.success(zhandianzhoubian);
    }

    @PostMapping
    public Result insert(@RequestBody Zhandianzhoubian zhandianzhoubian) {
        zhandianzhoubianService.insert(zhandianzhoubian);
        return Result.success();
    }

    @PutMapping
    public Result updateById(@RequestBody Zhandianzhoubian zhandianzhoubian) {
        zhandianzhoubianService.updateById(zhandianzhoubian);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable("id") Integer id) {
        zhandianzhoubianService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/page")
    public Result findByPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        List<Zhandianzhoubian> data = zhandianzhoubianService.findByPage(offset, pageSize);
        Page<Zhandianzhoubian> page = new Page<>();
        page.setData(data);
        page.setTotal(zhandianzhoubianService.count());
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        return Result.success(page);
    }

}
