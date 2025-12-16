package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.User;
import com.campus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 登录接口
    // URL: POST http://localhost:8080/user/login
    @PostMapping("/login")
    public Result<User> login(@RequestBody User user) {
        // @RequestBody 接收前端传来的 JSON 数据
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            return Result.success(loginUser);
        } else {
            return Result.error("用户名或密码错误");
        }
    }

    // 注册接口
    // URL: POST http://localhost:8080/user/register
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        boolean success = userService.register(user);
        if (success) {
            return Result.success("注册成功");
        } else {
            return Result.error("用户名已存在或注册失败");
        }
    }
}