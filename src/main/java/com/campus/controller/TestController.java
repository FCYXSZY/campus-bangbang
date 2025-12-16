package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.User;
import com.campus.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class TestController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/test")
    public Result<List<User>> test() {
        // 直接调用 MyBatis-Plus 提供的 selectList 方法
        List<User> list = userMapper.selectList(null);
        return Result.success(list);
    }
}