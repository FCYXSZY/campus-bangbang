package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password; // 实际开发建议加密，这里期末作业可以直接存明文
    private String nickname;
    private String phone;
    private BigDecimal balance; // 余额
    private Integer role; // 0:管理员 1:普通用户
    private Date createTime;

    private String avatar;
}