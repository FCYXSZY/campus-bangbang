package com.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.entity.TaskConsult;
import com.campus.mapper.TaskConsultMapper;
import com.campus.service.ConsultService;
import org.springframework.stereotype.Service;

@Service
public class ConsultServiceImpl extends ServiceImpl<TaskConsultMapper, TaskConsult> implements ConsultService {
}