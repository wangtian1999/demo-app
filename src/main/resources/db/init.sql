-- 创建数据库
CREATE DATABASE IF NOT EXISTS carbon_emission DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE carbon_emission;

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS `carbon_emission`;
DROP TABLE IF EXISTS `user`;

-- 用户表
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `role` VARCHAR(20) NOT NULL COMMENT '角色',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 碳排放数据表
CREATE TABLE `carbon_emission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `company` VARCHAR(100) NOT NULL COMMENT '企业名称',
    `type` VARCHAR(50) NOT NULL COMMENT '数据类型',
    `emission` DECIMAL(10,2) NOT NULL COMMENT '排放量(吨CO2)',
    `date` DATE NOT NULL COMMENT '记录时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_company` (`company`),
    KEY `idx_date` (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳排放数据表';

-- 插入测试碳排放数据
INSERT INTO `carbon_emission` (`company`, `type`, `emission`, `date`) VALUES 
('绿色科技有限公司', '工业排放', 125.50, '2024-01-15'),
('环保能源集团', '能源消耗', 89.20, '2024-01-16'),
('清洁制造企业', '交通排放', 67.80, '2024-01-17'),
('可持续发展公司', '工业排放', 156.30, '2024-01-18'),
('新能源科技', '能源消耗', 98.75, '2024-01-19'),
('绿色交通公司', '交通排放', 45.60, '2024-01-20');