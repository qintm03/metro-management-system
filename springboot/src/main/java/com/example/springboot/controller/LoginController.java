package com.example.springboot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.annotation.Resource;
import com.example.springboot.service.AdminService;
import com.example.springboot.service.UserService;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Account;


@RestController
@RequestMapping("/login")
public class LoginController {
    @Resource
    private UserService userService;
    @Resource
    private AdminService adminService;

    @PostMapping("")
    public Result login(@RequestBody Account account) {
        // 处理登录逻辑
        Account ac = null;
        if ("管理员".equals(account.getRole())) {
            ac = adminService.login(account);
        }
        if ("普通用户".equals(account.getRole())) {
            ac = userService.login(account);
        }
        if (ac == null) {
            return Result.error("登录失败，用户不存在");
        }
        return Result.success(ac);
    }

}
