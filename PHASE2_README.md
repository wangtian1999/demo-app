# 第二阶段开发完成 - 核心功能模块

## 开发内容概述

第二阶段主要完成了文件存储模块和数据管理模块的开发，为系统提供了完整的文件管理和碳排放数据管理功能。

## 新增功能

### 1. 文件存储模块

#### 功能特性
- 支持多种文件类型上传（PDF、DOC、DOCX、XLS、XLSX、PPT、PPTX、JPG、PNG等）
- 基于MinIO的分布式文件存储
- 文件大小限制（最大10MB）
- 文件类型安全验证
- 文件元数据管理

#### API接口
- `POST /api/files/upload` - 文件上传
- `GET /api/files/{fileId}` - 文件下载
- `DELETE /api/files/{fileId}` - 文件删除
- `GET /api/files/user` - 获取用户文件列表
- `GET /api/files/check-type` - 检查文件类型支持

### 2. 碳排放数据管理模块

#### 功能特性
- 完整的CRUD操作（创建、读取、更新、删除）
- 数据验证和约束
- 分页查询支持
- 多条件查询（企业名称、日期范围）
- 数据关联文件支持

#### API接口
- `POST /api/emissions` - 新增碳排放数据
- `PUT /api/emissions/{id}` - 更新碳排放数据
- `DELETE /api/emissions/{id}` - 删除碳排放数据
- `GET /api/emissions/{id}` - 获取单条数据
- `GET /api/emissions` - 分页查询数据
- `GET /api/emissions/all` - 获取所有数据
- `GET /api/emissions/enterprise/{enterpriseName}` - 按企业查询

## 技术架构

### 新增依赖
- **MinIO**: 分布式对象存储服务
- **Apache Tika**: 文件类型检测
- **Apache Commons Lang3**: 工具类库
- **Spring Boot Validation**: 数据验证

### 数据库设计

#### 文件存储表 (file_storage)
```sql
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
```

#### 碳排放数据表 (carbon_emission)
```sql
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
```

## 配置说明

### MinIO配置
```yaml
minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket-name: carbon-emission
```

### 文件上传配置
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
      enabled: true
```

## 部署要求

### 环境依赖
1. **MySQL 8.0+**: 数据库服务
2. **MinIO**: 对象存储服务
3. **Java 17+**: 运行环境
4. **Maven 3.6+**: 构建工具

### MinIO安装和配置

#### 使用Docker安装MinIO
```bash
docker run -p 9000:9000 -p 9001:9001 \
  --name minio \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  -v /mnt/data:/data \
  quay.io/minio/minio server /data --console-address ":9001"
```

#### 访问MinIO控制台
- 控制台地址: http://localhost:9001
- 用户名: minioadmin
- 密码: minioadmin

## 使用示例

### 文件上传示例
```bash
curl -X POST \
  http://localhost:8080/api/files/upload \
  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@/path/to/your/file.pdf'
```

### 碳排放数据创建示例
```bash
curl -X POST \
  http://localhost:8080/api/emissions \
  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{
    "enterpriseName": "测试企业",
    "emissionDate": "2024-01-20",
    "emissionAmount": 100.50
  }'
```

### 分页查询示例
```bash
curl -X GET \
  'http://localhost:8080/api/emissions?current=1&size=10&enterpriseName=绿色科技' \
  -H 'Authorization: Bearer YOUR_JWT_TOKEN'
```

## 安全特性

1. **文件类型验证**: 只允许预定义的安全文件类型
2. **文件大小限制**: 防止大文件攻击
3. **JWT认证**: 所有API都需要有效的JWT令牌
4. **数据验证**: 使用Bean Validation进行输入验证
5. **SQL注入防护**: 使用MyBatis-Plus的参数化查询

## 下一步计划

第三阶段将开发查询功能模块，包括：
- 高级搜索功能
- 同义词查询
- 数据导出功能
- 统计分析功能

## 注意事项

1. 确保MinIO服务正常运行
2. 数据库需要执行更新后的init.sql脚本
3. 文件上传需要有效的JWT认证
4. 建议在生产环境中修改MinIO的默认密钥
5. 定期备份文件存储和数据库数据