package com.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.entity.User;
import com.campus.mapper.UserMapper;
import com.campus.service.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User login(String username, String password) {
        // 1. 根据用户名查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = baseMapper.selectOne(queryWrapper);

        // 2. 判断用户是否存在，密码是否正确
        if (user != null && user.getPassword().equals(password)) {
            return user; // 登录成功
        }
        return null; // 登录失败
    }

    @Override
    public boolean register(User user) {
        // 1. 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        if (baseMapper.selectCount(queryWrapper) > 0) {
            return false; // 用户名已存在
        }

        // 2. 初始化新用户数据
        user.setRole(1); // 默认为普通学生
        user.setBalance(BigDecimal.ZERO); // 余额为0
        user.setCreateTime(new Date());

        // 3. 保存
        return save(user);
    }
}