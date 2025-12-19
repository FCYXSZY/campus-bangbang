package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.entity.Task;
import com.campus.entity.User;
import com.campus.service.TaskService;
import com.campus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;
    // 发布任务接口
    @PostMapping("/publish")
    @Transactional(rollbackFor = Exception.class) // 加上事务注解
    public Result<String> publish(@RequestBody Task task) {
        // 简单校验
        if (task.getSchoolId() == null) return Result.error("必须选择学校");

        if (task.getReward() == null || task.getTitle() == null) {
            return Result.error("标题和赏金不能为空");
        }
        // 1. 检查余额够不够
        User publisher = userService.getById(task.getPublisherId());
        if (publisher.getBalance().compareTo(task.getReward()) < 0) {
            return Result.error("余额不足，请先充值");
        }
        // 2. 扣钱
        publisher.setBalance(publisher.getBalance().subtract(task.getReward()));
        userService.updateById(publisher);
        // 3. 记录流水 (WalletLog) - 咱们之前建了表但没用，这里简单点就不写Log了，只改余额

        taskService.publishTask(task);
        return Result.success("发布成功，资金暂时中转，等待接单！");
    }

    // 任务大厅列表（分页查询）
    // URL: GET /task/list?page=1&size=10&schoolId=1
    @GetMapping("/list")
    public Result<Page<Task>> list(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   @RequestParam Long schoolId) {
        Page<Task> pageInfo = new Page<>(page, size);
        QueryWrapper<Task> wrapper = new QueryWrapper<>();

        if (schoolId != null) {
            wrapper.eq("school_id", schoolId);
        }

        wrapper.orderByAsc("status");
        wrapper.orderByDesc("create_time");

        // 1. 第一次查询（拿到原始数据）
        Page<Task> resultPage = taskService.page(pageInfo, wrapper);

        // 2. 填充数据（在原始数据上修改）
        for (Task task : resultPage.getRecords()) {
            User publisher = userService.getById(task.getPublisherId());
            if (publisher != null) {
                task.setPublisherAvatar(publisher.getAvatar());
                task.setPublisherName(publisher.getNickname());
            }
        }

        // 3. 【核心修改】直接返回处理好的 resultPage，不要再查一遍了！
        return Result.success(resultPage);
    }
    // 3. 抢单接口
    @PostMapping("/accept")
    public Result<String> accept(@RequestBody Map<String, Long> params) {
        Long taskId = params.get("taskId");
        Long userId = params.get("userId");
        try {
            taskService.acceptTask(taskId, userId);
            return Result.success("抢单成功！快去联系发布者吧");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 4. 查询我发布的
    @GetMapping("/my-publish")
    public Result<List<Task>> myPublish(@RequestParam Long userId) {
        QueryWrapper<Task> query = new QueryWrapper<>();
        query.eq("publisher_id", userId);
        query.orderByDesc("create_time");
        return Result.success(taskService.list(query));
    }

    // 5. 查询我抢到的
    @GetMapping("/my-accept")
    public Result<List<Task>> myAccept(@RequestParam Long userId) {
        QueryWrapper<Task> query = new QueryWrapper<>();
        query.eq("acceptor_id", userId);
        query.orderByDesc("create_time");
        return Result.success(taskService.list(query));
    }

    // 6. 接单者点击“我已送达”
    @PostMapping("/complete")
    public Result<String> complete(@RequestBody Map<String, Long> params) {
        Long taskId = params.get("taskId");
        Task task = taskService.getById(taskId);
        if (task.getStatus() != 1) return Result.error("任务状态异常");

        task.setStatus(2); // 2: 待确认
        taskService.updateById(task);
        return Result.success("已通知雇主验收");
    }

    // 7. 雇主点击“确认完成”
    @PostMapping("/confirm")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> confirm(@RequestBody Map<String, Long> params) {
        Long taskId = params.get("taskId");
        Task task = taskService.getById(taskId);
        if (task.getStatus() != 2) return Result.error("任务状态异常，需接单者先确认送达");

        task.setStatus(3); // 3: 已完成
        taskService.updateById(task);

        // TODO: 这里将来可以加扣款/打钱逻辑（已完成）
        // 2. 给接单者加钱
        User acceptor = userService.getById(task.getAcceptorId());
        acceptor.setBalance(acceptor.getBalance().add(task.getReward()));
        userService.updateById(acceptor);

        return Result.success("订单结束，感谢使用");
    }

    // 8. 雇主取消任务
    @PostMapping("/cancel")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> cancel(@RequestBody Map<String, Long> params) {
        Long taskId = params.get("taskId");
        Task task = taskService.getById(taskId);
        if (task.getStatus() != 0) return Result.error("任务已被抢或已结束，无法取消");

        task.setStatus(4); // 4: 已取消
        taskService.updateById(task);

        // 2. 退钱
        User publisher = userService.getById(task.getPublisherId());
        publisher.setBalance(publisher.getBalance().add(task.getReward()));
        userService.updateById(publisher);

        return Result.success("任务已取消，资金已退回");
    }
}