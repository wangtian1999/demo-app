# Simple Demo App

一个简单的Spring Boot演示应用程序。

## 技术栈

- **后端**: Spring Boot 3.5.0
- **Java版本**: JDK 17
- **构建工具**: Maven

## 环境要求

- JDK 17 或更高版本
- Maven 3.6+ 或使用项目自带的 Maven Wrapper

## 项目结构

```
src/
├── main/
│   └── java/
│       └── com/example/demo/
│           ├── DemoApplication.java     # 主启动类
│           └── controller/
│               └── HelloController.java # 简单的REST控制器
└── test/
    └── java/
        └── com/example/demo/
            └── DemoApplicationTests.java # 基础测试类
```

## 快速开始

### 1. 启动应用

```bash
# 使用Maven启动
mvn spring-boot:run

# 或使用Maven Wrapper
./mvnw spring-boot:run
```

### 2. 访问应用

应用启动后，可以访问以下端点：

- `http://localhost:8080/` - 欢迎页面
- `http://localhost:8080/hello` - 问候接口
- `http://localhost:8080/hello?name=张三` - 带参数的问候接口
- `http://localhost:8080/api/status` - 应用状态检查

### 3. 运行测试

```bash
# 运行所有测试
mvn test

# 或使用Maven Wrapper
./mvnw test
```

## 特性

- ✅ 简单的REST API
- ✅ 基础的Spring Boot配置
- ✅ 单元测试支持
- ✅ 最小化依赖
- ✅ 开箱即用

## 开发说明

这是一个极简的Spring Boot项目，专注于：

1. **简单性** - 最少的依赖和配置
2. **易用性** - 快速启动和部署
3. **可扩展性** - 可以根据需要添加更多功能

适合用作：
- Spring Boot学习项目
- 新项目的起始模板
- 微服务架构中的简单服务