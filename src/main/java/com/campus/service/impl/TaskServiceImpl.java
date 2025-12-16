package com.campus.service.impl;

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

        // TODO: 后面我们要在这里加上“扣除用户余额”的逻辑，现在先不做，先把流程跑通

        // 2. 保存到数据库
        return save(task);
    }
}