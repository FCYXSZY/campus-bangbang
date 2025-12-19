package com.campus.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("canteen")
public class Canteen {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long schoolId;
    private String name;
    private String location;
}