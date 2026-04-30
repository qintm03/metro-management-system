package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Checixianlu;
import com.example.springboot.entity.Page;
import com.example.springboot.service.ChecixianluService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checixianlu")
public class ChecixianluController {

    @Resource
    private ChecixianluService checixianluService;

    @GetMapping
    public Result findAll() {
        List<Checixianlu> list = checixianluService.findAll();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable("id") Integer id) {
        Checixianlu checixianlu = checixianluService.findById(id);
        return Result.success(checixianlu);
    }

    @PostMapping
    public Result insert(@RequestBody Checixianlu checixianlu) {
        checixianluService.insert(checixianlu);
        return Result.success();
    }

    @PutMapping
    public Result updateById(@RequestBody Checixianlu checixianlu) {
        checixianluService.updateById(checixianlu);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable("id") Integer id) {
        checixianluService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/page")
    public Result findByPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        List<Checixianlu> data = checixianluService.findByPage(offset, pageSize);
        Page<Checixianlu> page = new Page<>();
        page.setData(data);
        page.setTotal(checixianluService.count());
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        return Result.success(page);
    }

}
