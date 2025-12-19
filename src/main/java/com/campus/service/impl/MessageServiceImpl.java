package com.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.entity.SysMessage;
import com.campus.mapper.SysMessageMapper;
import com.campus.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage> implements MessageService {
}