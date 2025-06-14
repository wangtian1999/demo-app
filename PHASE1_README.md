# 第一阶段开发完成 - 用户认证模块

## 已完成功能

### 1. 用户认证系统
- ✅ JWT Token 生成和验证
- ✅ BCrypt 密码加密
- ✅ Spring Security 配置
- ✅ CORS 跨域配置
- ✅ 用户登录 API
- ✅ 用户登出 API
- ✅ Token 验证 API

### 2. 数据库设计
- ✅ 用户表结构
- ✅ 默认测试账号
- ✅ 数据库初始化脚本
- ✅ MyBatis-Plus 集成

### 3. 项目结构
```
src/main/java/com/example/demo/
├── config/
│   └── SecurityConfig.java          # Spring Security配置
├── controller/
│   ├── AuthController.java          # 认证控制器
│   └── HelloController.java         # 基础控制器
├── dto/
│   ├── LoginRequest.java            # 登录请求DTO
│   └── LoginResponse.java           # 登录响应DTO
├── entity/
│   └── User.java                    # 用户实体类
├── mapper/
│   └── UserMapper.java              # 用户数据访问层
├── service/
│   └── UserService.java             # 用户服务层
├── util/
│   └── JwtUtils.java                # JWT工具类
└── DemoApplication.java             # 主启动类
```

## API 接口

### 认证接口

#### 1. 用户登录
- **URL**: `POST /api/auth/login`
- **请求体**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```
- **响应**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "role": "ADMIN",
  "message": "登录成功"
}
```

#### 2. 用户登出
- **URL**: `POST /api/auth/logout`
- **响应**: `"登出成功"`

#### 3. Token验证
- **URL**: `GET /api/auth/validate?token=xxx`
- **响应**: `"令牌有效"` 或 `"令牌无效"`

### 基础接口
- `GET /` - 欢迎页面
- `GET /hello` - 问候接口
- `GET /api/status` - 状态检查

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | ADMIN |
| user | user123 | USER |

## 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.6+

## 启动步骤

### 1. 数据库准备
```sql
-- 创建数据库
CREATE DATABASE carbon_emission;

-- 执行初始化脚本
source src/main/resources/db/init.sql;
```

### 2. 配置数据库连接
修改 `application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/carbon_emission
    username: root
    password: 123456  # 修改为你的数据库密码
```

### 3. 启动应用
```bash
# 编译项目
mvn clean compile

# 启动应用
mvn spring-boot:run
```

### 4. 测试接口
```bash
# 测试登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 测试基础接口
curl http://localhost:8080/hello
```

## 技术特性

- **安全性**: BCrypt密码加密，JWT令牌认证
- **跨域支持**: 完整的CORS配置
- **数据库**: MyBatis-Plus ORM框架
- **配置**: 外部化配置支持
- **日志**: 详细的调试日志

## 下一阶段计划

第二阶段将开发：
1. 文件存储模块（MinIO集成）
2. 碳排放数据管理模块
3. 数据CRUD接口
4. 数据验证和处理功能

---

**注意**: 请确保MySQL服务正在运行，并且数据库连接配置正确。首次启动时会自动创建默认用户账号。