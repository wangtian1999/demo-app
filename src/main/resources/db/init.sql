-- 创建数据库
CREATE DATABASE IF NOT EXISTS carbon_emission DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE carbon_emission;

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS `carbon_emission`;
DROP TABLE IF EXISTS `file_storage`;
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

-- 文件存储表
CREATE TABLE `file_storage` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `file_name` VARCHAR(100) NOT NULL COMMENT '文件名称',
    `file_type` VARCHAR(20) NOT NULL COMMENT '文件类型',
    `file_url` VARCHAR(255) NOT NULL COMMENT '文件URL',
    `file_size` BIGINT NOT NULL COMMENT '文件大小（字节）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT COMMENT '创建者ID',
    PRIMARY KEY (`id`),
    KEY `idx_create_by` (`create_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件存储表';

-- 碳排放数据表
CREATE TABLE `carbon_emission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `enterprise_name` VARCHAR(100) NOT NULL COMMENT '企业名称',
    `emission_date` DATE NOT NULL COMMENT '排放日期',
    `emission_amount` DECIMAL(10,2) NOT NULL COMMENT '排放量（吨CO2）',
    `file_url` VARCHAR(255) COMMENT '关联文件URL',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建者ID',
    `update_by` BIGINT COMMENT '更新者ID',
    PRIMARY KEY (`id`),
    KEY `idx_enterprise_name` (`enterprise_name`),
    KEY `idx_emission_date` (`emission_date`),
    KEY `idx_create_by` (`create_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳排放数据表';

-- 插入测试碳排放数据
INSERT INTO `carbon_emission` (`enterprise_name`, `emission_date`, `emission_amount`, `create_by`, `update_by`) VALUES 
('绿色科技有限公司', '2024-01-15', 125.50, 1, 1),
('环保能源集团', '2024-01-16', 89.20, 1, 1),
('清洁制造企业', '2024-01-17', 67.80, 1, 1),
('可持续发展公司', '2024-01-18', 156.30, 1, 1),
('新能源科技', '2024-01-19', 98.75, 1, 1),
('绿色交通公司', '2024-01-20', 45.60, 1, 1),
('华能电力集团', '2024-01-21', 234.80, 1, 1),
('中石化炼油厂', '2024-01-22', 456.20, 1, 1),
('宝钢集团', '2024-01-23', 678.90, 1, 1),
('比亚迪汽车', '2024-01-24', 123.45, 1, 1),
('腾讯科技', '2024-01-25', 78.30, 1, 1),
('阿里巴巴集团', '2024-01-26', 92.15, 1, 1),
('海尔智家', '2024-01-27', 156.70, 1, 1),
('美的集团', '2024-01-28', 189.25, 1, 1),
('格力电器', '2024-01-29', 167.80, 1, 1),
('万科地产', '2024-01-30', 298.40, 1, 1),
('恒大集团', '2024-02-01', 345.60, 1, 1),
('中国移动', '2024-02-02', 87.90, 1, 1),
('中国联通', '2024-02-03', 76.50, 1, 1),
('中国电信', '2024-02-04', 82.30, 1, 1),
('京东物流', '2024-02-05', 134.70, 1, 1),
('顺丰速运', '2024-02-06', 145.20, 1, 1),
('三一重工', '2024-02-07', 267.80, 1, 1),
('中联重科', '2024-02-08', 234.50, 1, 1),
('徐工机械', '2024-02-09', 198.60, 1, 1),
('蒙牛乳业', '2024-02-10', 89.40, 1, 1);