package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("task_order") // 对应数据库表名
public class Task {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long publisherId; // 发布者ID
    private Long acceptorId;  // 接单者ID

    private String title;
    private String description;
    private String locationFrom;
    private String locationTo;
    private BigDecimal reward; // 赏金

    // 格式化时间，方便前端直接显示 yyyy-MM-dd HH:mm:ss
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deadline;

    private Integer status; // 0:待接单, 1:进行中, 2:待确认, 3:已完成, 4:已取消
    private Integer version; // 乐观锁版本号

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}