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

    // 刷新用户信息 (用于个人中心查看最新余额)
    @GetMapping("/info")
    public Result<User> info(@RequestParam Long userId) {
        return Result.success(userService.getById(userId));
    }

    // 新增：更新个人信息 (修改昵称、头像、手机号)
    @PostMapping("/update")
    public Result<User> update(@RequestBody User user) {
        // 这里的 updateById 是 MyBatis-Plus 自带的，它会自动根据 ID 更新非空字段
        boolean success = userService.updateById(user);
        if (success) {
            // 更新成功后，查出最新的数据返回给前端更新缓存
            return Result.success(userService.getById(user.getId()));
        } else {
            return Result.error("修改失败");
        }
    }
}