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


CREATE TABLE `task_comment` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `task_id` bigint(20) NOT NULL COMMENT '关联任务ID',
                                `user_id` bigint(20) NOT NULL COMMENT '评价人ID(发布者)',
                                `target_id` bigint(20) NOT NULL COMMENT '被评价人ID(接单者)',
                                `score` int(1) NOT NULL DEFAULT 5 COMMENT '评分(1-5星)',
                                `content` varchar(255) DEFAULT NULL COMMENT '评价内容',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务评价表';



CREATE TABLE `sys_notice` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `title` varchar(50) NOT NULL COMMENT '公告标题',
                              `content` text NOT NULL COMMENT '公告内容',
                              `admin_id` bigint(20) NOT NULL COMMENT '发布管理员ID',
                              `is_top` int(1) DEFAULT 0 COMMENT '是否置顶(0否 1是)',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';


-- 6. 任务咨询/留言表 (公开的，类似淘宝提问)
CREATE TABLE `task_consult` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `task_id` bigint(20) NOT NULL COMMENT '关联任务ID',
                                `user_id` bigint(20) NOT NULL COMMENT '提问者ID',
                                `username` varchar(50) DEFAULT NULL COMMENT '提问者名字快照',
                                `content` varchar(255) NOT NULL COMMENT '提问内容',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务咨询表';

-- 7. 站内私信表 (私密的)
CREATE TABLE `sys_message` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                               `sender_id` bigint(20) NOT NULL COMMENT '发送者ID',
                               `receiver_id` bigint(20) NOT NULL COMMENT '接收者ID',
                               `content` varchar(500) NOT NULL COMMENT '私信内容',
                               `is_read` int(1) DEFAULT 0 COMMENT '0:未读 1:已读',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内私信表';


-- 1. 新建学校表
CREATE TABLE `sys_school` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `name` varchar(100) NOT NULL COMMENT '学校名称',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 改造任务表：增加 school_id
ALTER TABLE `task_order` ADD COLUMN `school_id` bigint(20) DEFAULT NULL COMMENT '所属学校ID';

-- 3. 改造食堂相关表 (关联学校)
DROP TABLE IF EXISTS `canteen`; -- 如果之前有旧表，先删了重建
CREATE TABLE `canteen` (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT,
                           `school_id` bigint(20) NOT NULL COMMENT '所属学校',
                           `name` varchar(50) NOT NULL COMMENT '食堂名称(如一食堂)',
                           `location` varchar(100) DEFAULT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `canteen_window`;
CREATE TABLE `canteen_window` (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                  `canteen_id` bigint(20) NOT NULL COMMENT '所属食堂',
                                  `name` varchar(50) NOT NULL COMMENT '窗口名(如麻辣烫)',
                                  `floor` varchar(20) DEFAULT '1楼' COMMENT '楼层',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `dish`;
CREATE TABLE `dish` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `window_id` bigint(20) NOT NULL,
                        `name` varchar(50) NOT NULL,
                        `price` decimal(10,2) NOT NULL,
                        `image` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 初始化一点测试数据
INSERT INTO `sys_school` (id, name) VALUES (1, '清华大学'), (2, '北京大学'), (3, '蓝翔技校');

-- 给蓝翔技校(ID=3)加点食堂数据
INSERT INTO `canteen` (id, school_id, name) VALUES (1, 3, '挖掘机一食堂'), (2, 3, '美容美发二食堂');
INSERT INTO `canteen_window` (id, canteen_id, name, floor) VALUES (1, 1, '豪华挖掘机套餐', '1楼'), (2, 1, '机油炒饭', '2楼');
INSERT INTO `dish` (window_id, name, price) VALUES (1, '红烧螺丝钉', 12.00), (1, '清蒸齿轮', 8.50), (2, '92号汽油汤', 5.00);

-- 插入管理员
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `balance`) VALUES ('admin', '123456', '超管', 0, 99999.00);
-- 插入两个测试学生
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `balance`) VALUES ('stu01', '123456', '张三', 1, 100.00);
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `balance`) VALUES ('stu02', '123456', '李四', 1, 50.00);


ALTER TABLE `sys_user` MODIFY COLUMN `username` varchar(50); -- 顺便检查下
ALTER TABLE `sys_user` ADD COLUMN `avatar` LONGTEXT COMMENT '头像Base64';