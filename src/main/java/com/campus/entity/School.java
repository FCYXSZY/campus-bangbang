package com.campus.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("sys_school")
public class School {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
}