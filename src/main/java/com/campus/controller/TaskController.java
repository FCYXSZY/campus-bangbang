package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.entity.Task;
import com.campus.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // 发布任务接口
    @PostMapping("/publish")
    public Result<String> publish(@RequestBody Task task) {
        // 简单校验
        if (task.getReward() == null || task.getTitle() == null) {
            return Result.error("标题和赏金不能为空");
        }
        taskService.publishTask(task);
        return Result.success("发布成功，等待接单！");
    }

    // 任务大厅列表（分页查询）
    // URL: GET /task/list?page=1&size=10
    @GetMapping("/list")
    public Result<Page<Task>> list(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size) {
        // 1. 构造分页参数
        Page<Task> pageInfo = new Page<>(page, size);

        // 2. 查询条件：只查 status=0 (待接单) 的任务，且按创建时间倒序
        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        wrapper.orderByDesc("create_time");

        // 3. 执行查询
        Page<Task> resultPage = taskService.page(pageInfo, wrapper);

        return Result.success(resultPage);
    }
}