package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.common.Result;
import com.campus.entity.TaskConsult;
import com.campus.service.ConsultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/consult")
public class ConsultController {

    @Autowired
    private ConsultService consultService;

    // 发布留言
    @PostMapping("/add")
    public Result<String> add(@RequestBody TaskConsult consult) {
        // 设置当前时间
        consult.setCreateTime(new Date());
        consultService.save(consult);
        return Result.success("留言成功");
    }

    // 查看某任务下的所有留言
    @GetMapping("/list")
    public Result<List<TaskConsult>> list(@RequestParam Long taskId) {
        QueryWrapper<TaskConsult> query = new QueryWrapper<>();
        query.eq("task_id", taskId);
        // 按时间倒序（最新的在上面）或者正序，看你喜好
        query.orderByDesc("create_time");
        return Result.success(consultService.list(query));
    }
}