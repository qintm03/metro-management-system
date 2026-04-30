package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Discusschecixianlu;
import com.example.springboot.service.DiscusschecixianluService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discusschecixianlu")
public class DiscusschecixianluController {

    @Autowired
    private DiscusschecixianluService discusschecixianluService;

    @PostMapping
    public Result add(@RequestBody Discusschecixianlu discusschecixianlu) {
        int result = discusschecixianluService.add(discusschecixianlu);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("评论失败");
    }

    @PutMapping
    public Result update(@RequestBody Discusschecixianlu discusschecixianlu) {
        int result = discusschecixianluService.update(discusschecixianlu);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("更新失败");
    }

    @PostMapping("/reply")
    public Result reply(@RequestParam Integer id, @RequestParam String reply) {
        int result = discusschecixianluService.reply(id, reply);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("回复失败");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        int result = discusschecixianluService.delete(id);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("删除失败");
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        Discusschecixianlu discusschecixianlu = discusschecixianluService.findById(id);
        return Result.success(discusschecixianlu);
    }

    @GetMapping
    public Result findAll() {
        List<Discusschecixianlu> list = discusschecixianluService.findAll();
        return Result.success(list);
    }

    @GetMapping("/ref/{refid}")
    public Result findByRefid(@PathVariable Integer refid) {
        List<Discusschecixianlu> list = discusschecixianluService.findByRefid(refid);
        return Result.success(list);
    }

    @GetMapping("/user/{userid}")
    public Result findByUserid(@PathVariable Integer userid) {
        List<Discusschecixianlu> list = discusschecixianluService.findByUserid(userid);
        return Result.success(list);
    }

    @PostMapping("/search")
    public Result search(@RequestBody Discusschecixianlu discusschecixianlu) {
        List<Discusschecixianlu> list = discusschecixianluService.findByCondition(discusschecixianlu);
        return Result.success(list);
    }

    @GetMapping("/count/{refid}")
    public Result countByRefid(@PathVariable Integer refid) {
        Integer count = discusschecixianluService.countByRefid(refid);
        return Result.success(count);
    }

}
