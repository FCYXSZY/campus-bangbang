package com.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.entity.Task;
import com.campus.mapper.TaskMapper;
import com.campus.service.TaskService;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    @Override
    public boolean publishTask(Task task) {
        // 1. 补全默认信息
        task.setStatus(0); // 默认为待接单
        task.setVersion(1); // 版本号初始化
        task.setCreateTime(new Date());

        // TODO: 后面我们要在这里加上“扣除用户余额”的逻辑，现在先不做，先把流程跑通（已完成）

        // 2. 保存到数据库
        return save(task);
    }
    @Override
    public boolean acceptTask(Long taskId, Long userId) {
        // 1. 查出任务
        Task task = getById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        // 2. 校验状态 (必须是 0-待接单)
        if (task.getStatus() != 0) {
            throw new RuntimeException("手慢了，任务已被抢走！");
        }

        // 3. 校验是不是自己抢自己的 (可选)
        if (task.getPublisherId().equals(userId)) {
            throw new RuntimeException("不能抢自己发布的任务！");
        }

        // 4. 执行抢单 (利用 MyBatis-Plus 的 UpdateWrapper 进行乐观锁更新)
        // SQL 等价于: UPDATE task_order SET acceptor_id=?, status=1, version=version+1
        //             WHERE id=? AND version=?
        UpdateWrapper<Task> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", taskId);
        updateWrapper.eq("version", task.getVersion()); // 核心：检查版本号是否没变
        updateWrapper.set("acceptor_id", userId);
        updateWrapper.set("status", 1); // 变更为进行中
        updateWrapper.set("version", task.getVersion() + 1); // 版本号+1

        boolean success = update(updateWrapper);
        if (!success) {
            throw new RuntimeException("并发抢单失败，请重试");
        }
        return true;
    }
}