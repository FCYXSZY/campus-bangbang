CREATE DATABASE IF NOT EXISTS campus_help_db DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
USE campus_help_db;

-- 1. 用户表
CREATE TABLE `sys_user` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `username` varchar(50) NOT NULL COMMENT '学号/用户名',
                            `password` varchar(100) NOT NULL,
                            `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
                            `phone` varchar(20) DEFAULT NULL,
                            `balance` decimal(10,2) DEFAULT 0.00 COMMENT '钱包余额',
                            `role` int(2) DEFAULT 1 COMMENT '0:管理员, 1:普通用户',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 任务订单表 (核心表)
CREATE TABLE `task_order` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `publisher_id` bigint(20) NOT NULL COMMENT '发布者ID',
                              `acceptor_id` bigint(20) DEFAULT NULL COMMENT '接单者ID(未接单为空)',
                              `title` varchar(50) NOT NULL COMMENT '任务标题(如:取快递)',
                              `description` varchar(255) DEFAULT NULL COMMENT '任务详情',
                              `location_from` varchar(100) DEFAULT NULL COMMENT '取件地',
                              `location_to` varchar(100) DEFAULT NULL COMMENT '送达地',
                              `reward` decimal(10,2) NOT NULL COMMENT '悬赏金额',
                              `deadline` datetime DEFAULT NULL COMMENT '截止时间',
                              `status` int(2) DEFAULT 0 COMMENT '0:待接单, 1:进行中, 2:待确认, 3:已完成, 4:已取消',
                              `version` int(11) DEFAULT 0 COMMENT '乐观锁版本号(用于并发抢单)',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                              `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务订单表';

-- 3. 资金流水表 (体现专业性)
CREATE TABLE `wallet_log` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `user_id` bigint(20) NOT NULL,
                              `type` int(2) NOT NULL COMMENT '1:充值, 2:发布扣款, 3:任务收益, 4:提现',
                              `amount` decimal(10,2) NOT NULL COMMENT '变动金额(+/-)',
                              `order_id` bigint(20) DEFAULT NULL COMMENT '关联的任务ID',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金流水表';

-- 插入管理员
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `balance`) VALUES ('admin', '123456', '超管', 0, 99999.00);
-- 插入两个测试学生
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `balance`) VALUES ('stu01', '123456', '张三', 1, 100.00);
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `balance`) VALUES ('stu02', '123456', '李四', 1, 50.00);