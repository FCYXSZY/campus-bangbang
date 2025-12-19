package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.common.Result;
import com.campus.entity.*;
import com.campus.mapper.*; // 记得建对应的Mapper
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/school")
public class SchoolController {

    @Autowired private SchoolMapper schoolMapper; // 记得创建 Mapper 接口
    @Autowired private CanteenMapper canteenMapper;
    @Autowired private CanteenWindowMapper windowMapper;
    @Autowired private DishMapper dishMapper;

    // 1. 获取所有学校列表
    @GetMapping("/list")
    public Result<List<School>> list() {
        return Result.success(schoolMapper.selectList(null));
    }

    // 2. 获取某学校的所有食堂
    @GetMapping("/canteen/list")
    public Result<List<Canteen>> canteenList(@RequestParam Long schoolId) {
        return Result.success(canteenMapper.selectList(new QueryWrapper<Canteen>().eq("school_id", schoolId)));
    }

    // 3. 获取某食堂的所有窗口 (顺便把菜品也查出来，做成级联)
    @GetMapping("/window/list")
    public Result<List<CanteenWindow>> windowList(@RequestParam Long canteenId) {
        List<CanteenWindow> windows = windowMapper.selectList(new QueryWrapper<CanteenWindow>().eq("canteen_id", canteenId));
        // 循环查菜品 (偷懒写法，生产环境请用联表)
        for (CanteenWindow win : windows) {
            win.setDishList(dishMapper.selectList(new QueryWrapper<Dish>().eq("window_id", win.getId())));
        }
        return Result.success(windows);
    }
}