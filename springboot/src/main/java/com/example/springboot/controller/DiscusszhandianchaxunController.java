package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Discusszhandianchaxun;
import com.example.springboot.service.DiscusszhandianchaxunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discusszhandianchaxun")
public class DiscusszhandianchaxunController {

    @Autowired
    private DiscusszhandianchaxunService discusszhandianchaxunService;

    @PostMapping
    public Result add(@RequestBody Discusszhandianchaxun discusszhandianchaxun) {
        int result = discusszhandianchaxunService.add(discusszhandianchaxun);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("评论失败");
    }

    @PutMapping
    public Result update(@RequestBody Discusszhandianchaxun discusszhandianchaxun) {
        int result = discusszhandianchaxunService.update(discusszhandianchaxun);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("更新失败");
    }

    @PostMapping("/reply")
    public Result reply(@RequestParam Integer id, @RequestParam String reply) {
        int result = discusszhandianchaxunService.reply(id, reply);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("回复失败");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        int result = discusszhandianchaxunService.delete(id);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("删除失败");
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        Discusszhandianchaxun discusszhandianchaxun = discusszhandianchaxunService.findById(id);
        return Result.success(discusszhandianchaxun);
    }

    @GetMapping
    public Result findAll() {
        List<Discusszhandianchaxun> list = discusszhandianchaxunService.findAll();
        return Result.success(list);
    }

    @GetMapping("/ref/{refid}")
    public Result findByRefid(@PathVariable Integer refid) {
        List<Discusszhandianchaxun> list = discusszhandianchaxunService.findByRefid(refid);
        return Result.success(list);
    }

    @GetMapping("/user/{userid}")
    public Result findByUserid(@PathVariable Integer userid) {
        List<Discusszhandianchaxun> list = discusszhandianchaxunService.findByUserid(userid);
        return Result.success(list);
    }

    @PostMapping("/search")
    public Result search(@RequestBody Discusszhandianchaxun discusszhandianchaxun) {
        List<Discusszhandianchaxun> list = discusszhandianchaxunService.findByCondition(discusszhandianchaxun);
        return Result.success(list);
    }

    @GetMapping("/count/{refid}")
    public Result countByRefid(@PathVariable Integer refid) {
        Integer count = discusszhandianchaxunService.countByRefid(refid);
        return Result.success(count);
    }

}
