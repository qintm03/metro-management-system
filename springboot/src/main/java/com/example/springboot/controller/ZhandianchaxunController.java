package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Page;
import com.example.springboot.entity.Zhandianchaxun;
import com.example.springboot.service.ZhandianchaxunService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/zhandianchaxun")
public class ZhandianchaxunController {

    @Resource
    private ZhandianchaxunService zhandianchaxunService;

    @GetMapping
    public Result findAll() {
        List<Zhandianchaxun> list = zhandianchaxunService.findAll();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable("id") Integer id) {
        Zhandianchaxun zhandianchaxun = zhandianchaxunService.findById(id);
        return Result.success(zhandianchaxun);
    }

    @PostMapping
    public Result insert(@RequestBody Zhandianchaxun zhandianchaxun) {
        zhandianchaxunService.insert(zhandianchaxun);
        return Result.success();
    }

    @PutMapping
    public Result updateById(@RequestBody Zhandianchaxun zhandianchaxun) {
        zhandianchaxunService.updateById(zhandianchaxun);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable("id") Integer id) {
        zhandianchaxunService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/page")
    public Result findByPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        List<Zhandianchaxun> data = zhandianchaxunService.findByPage(offset, pageSize);
        Page<Zhandianchaxun> page = new Page<>();
        page.setData(data);
        page.setTotal(zhandianchaxunService.count());
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        return Result.success(page);
    }

}
