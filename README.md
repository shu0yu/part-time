# 大学生兼职招聘平台

一个基于 Spring Boot + Vue 3 的全栈兼职招聘平台，面向大学生和企业提供岗位发布、在线申请、实时沟通的一站式服务，内置管理员后台实现平台治理。

## 项目概述

本项目是一个完整的 B2C 兼职招聘平台，支持**学生、企业、管理员**三种角色协同工作。企业发布兼职岗位，学生浏览并投递申请，双方通过内置的 **WebSocket 实时聊天**系统在线沟通。管理员拥有完整的后台管理能力，包括用户审核、岗位下架和纠纷仲裁。

**数据规模**：预置 100+ 兼职岗位、200+ 条聊天记录、16 个多角色用户，可直接用于演示。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.5.9 |
| 语言 | Java | 21 |
| ORM | MyBatis-Plus | 3.5.5 |
| 数据库 | MySQL | 8.0 |
| 连接池 | Druid | 1.2.20 |
| 认证鉴权 | JWT (Auth0 java-jwt) + 自定义 `@RequiredRole` 注解 | 4.4.0 |
| 即时通讯 | Spring WebSocket | — |
| API 文档 | Knife4j (OpenAPI 3.0) | 4.5.0 |
| 前端框架 | Vue 3 + Vite | — |
| UI 组件库 | Element Plus | — |
| 状态管理 | Pinia | — |
| 路由 | Vue Router 4 | — |

## 功能模块

### 学生端

- **岗位浏览与搜索**：支持按岗位名称、工作地点、岗位类型、薪资范围等多条件组合检索
- **在线投递**：一键投递简历并附言，查看申请记录
- **实时沟通**：WebSocket 即时聊天，与企业 HR 直接对话
- **个人信息**：维护学校、专业、年级等学籍信息

### 企业端

- **岗位管理**：发布、编辑、删除（软删除）兼职岗位，设置薪资范围、工作时间、岗位要求
- **人才筛选**：查看学生投递记录与申请附言
- **即时通讯**：接收学生咨询，发起实时对话
- **企业资料**：维护企业名称、地址、联系人等公开信息

### 管理员端

- **用户管理**：查看全部用户列表（含角色信息），按角色和状态筛选，启用/禁用账号
- **岗位管理**：查看全部岗位，支持下架违规岗位
- **纠纷仲裁**：查看任意学生与企业之间的完整聊天记录和申请记录，用于调解纠纷
- **会话监控**：查看平台所有聊天会话

### 系统能力

- **JWT 认证 + RBAC 权限**：基于注解的 `@RequiredRole` 角色拦截，方法级和类级双重控制
- **WebSocket 实时推送**：`ConcurrentHashMap` 管理在线会话，离线消息持久化到 MySQL
- **统一异常处理**：`@ControllerAdvice` 全局异常拦截，统一 `Result<T>` 响应格式
- **分页封装**：`PageResultBean` 统一分页响应结构
- **API 文档**：Knife4j 自动生成接口文档，支持在线调试

## 系统架构

```
┌─────────────────────────────────────────────────────┐
│                    Frontend (Vue 3)                   │
│  Element Plus  │  Pinia  │  Vue Router  │  Axios     │
│                 WebSocket Client                      │
└─────────────────────┬───────────────────────────────┘
                      │ HTTP (REST) + WebSocket
┌─────────────────────▼───────────────────────────────┐
│               Spring Boot (Java 21)                   │
│  ┌──────────┐  ┌──────────┐  ┌───────────────────┐  │
│  │ JWT 认证  │  │ RBAC 权限 │  │ WebSocket Handler │  │
│  │ Interceptor│ │ @Role    │  │ (ConcurrentHashMap)│  │
│  └──────────┘  └──────────┘  └───────────────────┘  │
│  ┌──────────────────────────────────────────────┐    │
│  │  Controller → Service → Mapper (MyBatis-Plus) │    │
│  └──────────────────────────────────────────────┘    │
└─────────────────────┬───────────────────────────────┘
                      │ JDBC (Druid 连接池)
┌─────────────────────▼───────────────────────────────┐
│                   MySQL 8.0                           │
│  user │ role │ user_role │ job │ job_application     │
│  company_info │ student_info │ chat_record           │
└─────────────────────────────────────────────────────┘
```

## 项目结构

```
part-time-public/
├── frontend/                        # Vue 3 前端
│   └── src/
│       ├── api/                     # Axios 接口封装
│       ├── components/              # 通用组件（ChatWindow, JobCard, JobForm 等）
│       ├── views/                   # 页面视图（14 个页面）
│       ├── router/                  # Vue Router 路由配置
│       ├── store/                   # Pinia 状态管理
│       └── utils/                   # WebSocket 客户端、安全过滤、性能工具
├── src/main/
│   ├── java/com/example/bishe_demo/
│   │   ├── annotation/              # 自定义注解 (@RequiredRole)
│   │   ├── config/                  # Spring 配置（WebSocket, Knife4j, MyBatis-Plus, CORS）
│   │   ├── controller/              # REST 控制器（7 个模块）
│   │   ├── service/                 # 业务逻辑层
│   │   ├── mapper/                  # MyBatis-Plus 数据访问层
│   │   ├── entity/                  # 数据实体（8 个表）
│   │   ├── vo/                      # 视图对象
│   │   ├── websocket/               # WebSocket 消息处理器
│   │   ├── interceptor/             # JWT 登录拦截器
│   │   └── utils/                   # 工具类（JWT, MD5, ThreadLocal）
│   └── resources/
│       ├── mock_data_init.sql       # 数据库初始化 + 演示数据
│       └── application.yml.example  # 配置文件模板
├── pom.xml                          # Maven 依赖管理
└── package.json                     # 前端依赖
```

## 快速开始

### 环境要求

- JDK 21+
- Node.js 18+
- MySQL 8.0+
- Maven（或使用项目内置的 Maven Wrapper）

### 1. 数据库初始化

```sql
CREATE DATABASE bishe_demo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

执行 `src/main/resources/mock_data_init.sql` 创建表结构并插入演示数据。

### 2. 后端配置

```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

编辑 `application.yml`，填入你的 MySQL 用户名和密码：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bishe_demo
    username: root
    password: your_password
```

### 3. 启动后端

```bash
./mvnw spring-boot:run
```

API 服务运行在 http://localhost:9999，Knife4j 接口文档访问 http://localhost:9999/doc.html

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端运行在 http://localhost:5173

### 5. 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 企业 HR | huawei_hr | 123456 |
| 学生 | student001 | 123456 |

> 所有预置账号密码均为 `123456`（MD5 加密存储）。

## License

MIT License — 详见 [LICENSE](LICENSE)
