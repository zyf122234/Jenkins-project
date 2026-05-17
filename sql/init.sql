-- ================================================
-- 微服务 Jenkins-Docker 项目 - 数据库初始化脚本
-- ================================================

-- 创建用户数据库
CREATE DATABASE IF NOT EXISTS `user_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建订单数据库
CREATE DATABASE IF NOT EXISTS `order_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ================================================
-- 用户表
-- ================================================
USE `user_db`;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入测试数据
INSERT INTO `user` (`username`, `email`, `phone`) VALUES
('张三', 'zhangsan@example.com', '13800001111'),
('李四', 'lisi@example.com', '13800002222'),
('王五', 'wangwu@example.com', '13800003333'),
('赵六', 'zhaoliu@example.com', '13800004444'),
('钱七', 'qianqi@example.com', '13800005555');

-- ================================================
-- 订单表
-- ================================================
USE `order_db`;

DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `quantity` INT DEFAULT 1 COMMENT '数量',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `status` VARCHAR(20) DEFAULT 'CREATED' COMMENT '订单状态: CREATED/PAID/SHIPPED/COMPLETED/CANCELLED',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 插入测试数据
INSERT INTO `orders` (`user_id`, `product_name`, `quantity`, `amount`, `status`) VALUES
(1, 'iPhone 15', 1, 6999.00, 'CREATED'),
(1, 'AirPods Pro', 2, 1899.00, 'PAID'),
(2, 'MacBook Pro 14', 1, 14999.00, 'SHIPPED'),
(2, 'iPad Air', 1, 4799.00, 'CREATED'),
(3, 'Apple Watch Ultra', 1, 6299.00, 'COMPLETED'),
(4, 'HomePod mini', 3, 749.00, 'CREATED'),
(5, 'Magic Keyboard', 1, 2499.00, 'PAID');
