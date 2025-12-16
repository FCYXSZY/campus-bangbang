package com.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.entity.User;

public interface UserService extends IService<User> {
    // 这里可以定义复杂的业务逻辑接口，比如登录
    User login(String username, String password);

    // 注册
    boolean register(User user);
}