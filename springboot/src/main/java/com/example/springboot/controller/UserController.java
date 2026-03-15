package com.example.springboot.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.annotation.Resource;

import com.example.springboot.service.UserService;

import com.example.springboot.common.Result;
import com.example.springboot.entity.User;
import com.example.springboot.entity.Page;
import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController // 返回jeson数据
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /*
     * 查询所有用户
     */
    @GetMapping()
    public Result findAll() {
        List<User> list = userService.findAll();
        return Result.success(list);
    }

    /*
     * 新增用户
     */
    @PostMapping("")
    public Result addUser(@RequestBody User user) {
        userService.addUser(user);
        List<User> list = userService.findAll();
        return Result.success(list);
    }

    /*
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable("id") Long id) {
        userService.deleteById(id);
        List<User> list = userService.findAll();
        return Result.success(list);
    }

    /*
     * 根据id更新用户
     */
    @PutMapping("")
    public Result updataById(@RequestBody User user) {
        userService.updataById(user);
        List<User> list = userService.findAll();
        return Result.success(list);

    }

    /*
     * 分页查询
     */
    @GetMapping("/page")
    public Result findByPage(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        List<User> userData = userService.findByPage(offset, pageSize);
        Page<User> page = new Page<>();
        page.setData(userData);

        Integer total = userService.count();
        page.setTotal(total);
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        return Result.success(page);
    }

}
