package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Messages;
import com.example.springboot.service.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessagesController {

    @Autowired
    private MessagesService messagesService;

    @PostMapping
    public Result add(@RequestBody Messages messages) {
        int result = messagesService.add(messages);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("留言失败");
    }

    @PutMapping
    public Result update(@RequestBody Messages messages) {
        int result = messagesService.update(messages);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("更新失败");
    }

    @PostMapping("/reply")
    public Result reply(@RequestParam Integer id,
                        @RequestParam String reply,
                        @RequestParam(required = false) String rpicture) {
        int result = messagesService.reply(id, reply, rpicture);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("回复失败");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        int result = messagesService.delete(id);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("删除失败");
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        Messages messages = messagesService.findById(id);
        return Result.success(messages);
    }

    @GetMapping
    public Result findAll() {
        List<Messages> list = messagesService.findAll();
        return Result.success(list);
    }

    @GetMapping("/user/{userid}")
    public Result findByUserid(@PathVariable Integer userid) {
        List<Messages> list = messagesService.findByUserid(userid);
        return Result.success(list);
    }

    @GetMapping("/unreplied")
    public Result findUnreplied() {
        List<Messages> list = messagesService.findUnreplied();
        return Result.success(list);
    }

    @PostMapping("/search")
    public Result search(@RequestBody Messages messages) {
        List<Messages> list = messagesService.findByCondition(messages);
        return Result.success(list);
    }

    @GetMapping("/count/{userid}")
    public Result countByUserid(@PathVariable Integer userid) {
        Integer count = messagesService.countByUserid(userid);
        return Result.success(count);
    }

    @GetMapping("/count/unreplied")
    public Result countUnreplied() {
        Integer count = messagesService.countUnreplied();
        return Result.success(count);
    }

}
