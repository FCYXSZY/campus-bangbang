package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.Task;

public interface TaskMapper extends BaseMapper<Task> {
    // 以后复杂的SQL写在这里，现在用MyBatis-Plus自带的就够了
}