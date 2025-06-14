# 吉林省制造企业碳排放数据资源聚合系统开发计划

## 一、系统架构设计
### 1. 技术栈选择
- 前端：React + TypeScript + Ant Design
- 后端：Spring Boot + MyBatis-Plus
- 数据库：MySQL
- 文件存储：MinIO

### 2. 系统模块划分
- 用户认证模块
- 文件存储模块
- 数据管理模块
- 查询检索模块

## 二、开发阶段规划

### 第一阶段：基础架构搭建（预计2周）
1. 项目初始化
   - 前端项目搭建
   - 后端项目搭建
   - 数据库设计
   - 文件存储服务配置

2. 用户认证模块
   - 用户登录功能
   - 权限控制实现
   - 登录状态管理

### 第二阶段：核心功能开发（预计3周）
1. 文件存储模块
   - 文档上传功能（PDF、DOCX等）
   - 图片上传功能（JPG、PNG等）
   - 文件存储管理

2. 数据管理模块
   - 碳排放数据结构设计
   - 数据CRUD接口开发
   - 数据验证与处理

### 第三阶段：查询功能开发（预计2周）
1. 基础查询功能
   - 多条件查询实现
   - 查询结果展示
   - 分页功能

2. 同义词查询功能
   - 同义词词典集成
   - 同义词匹配算法
   - 查询结果优化

### 第四阶段：系统优化与测试（预计1周）
1. 性能优化
   - 查询性能优化
   - 文件上传优化
   - 缓存策略实现

2. 系统测试
   - 功能测试
   - 性能测试
   - 安全测试

## 三、数据库设计（初步）
```sql
-- 用户表
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    create_time DATETIME,
    update_time DATETIME
);

-- 碳排放数据表
CREATE TABLE carbon_emission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    enterprise_name VARCHAR(100) NOT NULL,
    emission_date DATE NOT NULL,
    emission_amount DECIMAL(10,2) NOT NULL,
    file_url VARCHAR(255),
    create_time DATETIME,
    update_time DATETIME,
    create_by BIGINT,
    update_by BIGINT
);

-- 文件存储表
CREATE TABLE file_storage (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_name VARCHAR(100) NOT NULL,
    file_type VARCHAR(20) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    create_time DATETIME,
    create_by BIGINT
);
```

## 四、API接口设计（初步）
### 1. 用户认证接口
- POST /api/auth/login - 用户登录
- POST /api/auth/logout - 用户登出

### 2. 文件管理接口
- POST /api/files/upload - 文件上传
- GET /api/files/{id} - 文件下载
- DELETE /api/files/{id} - 文件删除

### 3. 碳排放数据接口
- POST /api/emissions - 新增数据
- PUT /api/emissions/{id} - 更新数据
- DELETE /api/emissions/{id} - 删除数据
- GET /api/emissions - 查询数据

## 五、注意事项
1. 安全性考虑
   - 密码加密存储
   - 文件上传安全控制
   - 接口访问权限控制

2. 性能考虑
   - 大文件上传优化
   - 查询性能优化
   - 缓存策略

3. 可扩展性考虑
   - 模块化设计
   - 接口版本控制
   - 配置外部化 