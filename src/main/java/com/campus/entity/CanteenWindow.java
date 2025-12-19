package com.campus.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.List;

@Data
@TableName("canteen_window")
public class CanteenWindow {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long canteenId;
    private String name;
    private String floor;

    // 这个字段数据库里没有，是用来给前端展示菜品列表的
    @TableField(exist = false)
    private List<Dish> dishList;
}