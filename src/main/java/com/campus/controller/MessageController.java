package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.common.Result;
import com.campus.entity.SysMessage;
import com.campus.entity.User;
import com.campus.service.MessageService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.campus.service.UserService; // 别忘了导包

import java.util.*;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;
    // 【核心】注入 UserService，用来查头像
    @Autowired
    private UserService userService;
    // ================== 1. 发送私信 ==================
    @PostMapping("/send")
    public Result<String> send(@RequestBody SysMessage msg) {
        msg.setCreateTime(new Date());
        msg.setIsRead(0); // 默认未读
        messageService.save(msg);
        return Result.success("发送成功");
    }

    // ================== 2. 获取聊天历史 (点进聊天框看所有) ==================
    /**
     * 核心逻辑：
     * 查询 (sender=A AND receiver=B) OR (sender=B AND receiver=A)
     * 按时间正序排列 (旧的在上面，新的在下面)
     */
    // 1. 获取聊天记录 (填充头像)
    @GetMapping("/history")
    public Result<List<SysMessage>> history(@RequestParam Long userId1, @RequestParam Long userId2) {
        QueryWrapper<SysMessage> query = new QueryWrapper<>();
        query.and(wrapper -> wrapper
                .eq("sender_id", userId1).eq("receiver_id", userId2)
                .or()
                .eq("sender_id", userId2).eq("receiver_id", userId1)
        );
        query.orderByAsc("create_time");

        List<SysMessage> list = messageService.list(query);

        // --- 循环填充头像 ---
        for (SysMessage msg : list) {
            User sender = userService.getById(msg.getSenderId());
            if (sender != null) {
                msg.setSenderAvatar(sender.getAvatar());
            }
        }
        // ------------------

        return Result.success(list);
    }

    // ================== 3. 获取消息列表 (Tab页看最近联系人) ==================
    /**
     * 核心逻辑：
     * 就像微信首页，只显示每个人最后发给你的一条消息
     */
    // 2. 获取消息列表 (填充头像)
    @GetMapping("/inbox")
    public Result<List<InboxVo>> inbox(@RequestParam Long userId) {
        QueryWrapper<SysMessage> query = new QueryWrapper<>();
        query.eq("receiver_id", userId);
        query.orderByDesc("create_time");
        List<SysMessage> allMsgs = messageService.list(query);

        List<InboxVo> list = new ArrayList<>();
        Set<Long> processedSenderIds = new HashSet<>();

        for (SysMessage msg : allMsgs) {
            if (!processedSenderIds.contains(msg.getSenderId())) {
                InboxVo vo = new InboxVo();
                vo.setTargetId(msg.getSenderId());
                vo.setLastContent(msg.getContent());
                vo.setLastTime(msg.getCreateTime());

                // --- 查对方的信息 (名字+头像) ---
                User target = userService.getById(msg.getSenderId());
                if (target != null) {
                    vo.setTargetName(target.getNickname()); // 用昵称
                    vo.setTargetAvatar(target.getAvatar()); // 用头像
                } else {
                    vo.setTargetName("用户" + msg.getSenderId());
                }
                // -----------------------------

                list.add(vo);
                processedSenderIds.add(msg.getSenderId());
            }
        }
        return Result.success(list);
    }

    // 内部类，用于返回给前端的消息列表对象
    @Data
    static class InboxVo {
        private Long targetId;      // 对方ID
        private String targetName;  // 对方名字
        private String lastContent; // 最后一条消息内容
        private Date lastTime;      // 最后一条时间
        // 【新增】对方头像
        private String targetAvatar;
    }
}