package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Discusszhandianzhoubian;
import com.example.springboot.service.DiscusszhandianzhoubianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discusszhandianzhoubian")
public class DiscusszhandianzhoubianController {

    @Autowired
    private DiscusszhandianzhoubianService discusszhandianzhoubianService;

    @PostMapping
    public Result add(@RequestBody Discusszhandianzhoubian discusszhandianzhoubian) {
        int result = discusszhandianzhoubianService.add(discusszhandianzhoubian);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("评论失败");
    }

    @PutMapping
    public Result update(@RequestBody Discusszhandianzhoubian discusszhandianzhoubian) {
        int result = discusszhandianzhoubianService.update(discusszhandianzhoubian);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("更新失败");
    }

    @PostMapping("/reply")
    public Result reply(@RequestParam Integer id, @RequestParam String reply) {
        int result = discusszhandianzhoubianService.reply(id, reply);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("回复失败");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        int result = discusszhandianzhoubianService.delete(id);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("删除失败");
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        Discusszhandianzhoubian discusszhandianzhoubian = discusszhandianzhoubianService.findById(id);
        return Result.success(discusszhandianzhoubian);
    }

    @GetMapping
    public Result findAll() {
        List<Discusszhandianzhoubian> list = discusszhandianzhoubianService.findAll();
        return Result.success(list);
    }

    @GetMapping("/ref/{refid}")
    public Result findByRefid(@PathVariable Integer refid) {
        List<Discusszhandianzhoubian> list = discusszhandianzhoubianService.findByRefid(refid);
        return Result.success(list);
    }

    @GetMapping("/user/{userid}")
    public Result findByUserid(@PathVariable Integer userid) {
        List<Discusszhandianzhoubian> list = discusszhandianzhoubianService.findByUserid(userid);
        return Result.success(list);
    }

    @PostMapping("/search")
    public Result search(@RequestBody Discusszhandianzhoubian discusszhandianzhoubian) {
        List<Discusszhandianzhoubian> list = discusszhandianzhoubianService.findByCondition(discusszhandianzhoubian);
        return Result.success(list);
    }

    @GetMapping("/count/{refid}")
    public Result countByRefid(@PathVariable Integer refid) {
        Integer count = discusszhandianzhoubianService.countByRefid(refid);
        return Result.success(count);
    }

}
