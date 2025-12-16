package com.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.entity.Task;

public interface TaskService extends IService<Task> {
    // 发布任务
    boolean publishTask(Task task);
}