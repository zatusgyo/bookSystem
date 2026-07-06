# 图书借阅与销售平台 — 系统设计文档

> **版本**: v1.0 | **日期**: 2026-07-06 | **作者**: 组长（系统设计）

---

## 一、项目概述

### 1.1 项目背景
开发一个集图书借阅与销售于一体的在线平台，为用户提供便捷的图书浏览、借阅、购买服务。

### 1.2 核心功能
| 模块 | 功能 |
|------|------|
| **图书借阅** | 浏览搜索、在线借阅、归还、续借，支持单人借阅 / 多人共读 |
| **图书销售** | 在线购物车、下单、支付（支付宝/微信）、订单管理、物流跟踪 |
| **用户系统** | 注册/登录、个人信息管理、角色权限（管理员 / 会员 / VIP） |

### 1.3 技术选型
| 层级 | 技术 | 版本 |
|------|------|------|
| 前端 | Vue 3 + Element Plus + Pinia + Axios | Vue 3.4+ |
| 后端 | Spring Boot + MyBatis-Plus | 2.7.18 |
| 数据库 | MySQL | 8.0+ |
| 接口文档 | Knife4j (Swagger) | 4.1.0 |
| 构建工具 | Maven / Vite | — |

---

## 二、系统架构

### 2.1 整体架构

```
┌─────────────────────────────────────────────┐
│                 前端 (Vue 3)                 │
│         Element Plus + Pinia + Axios         │
├─────────────────────────────────────────────┤
│              HTTP REST API (:8080)           │
├─────────────────────────────────────────────┤
│            后端 (Spring Boot)                │
│  Controller → Service → Mapper → MySQL       │
├─────────────────────────────────────────────┤
│                  MySQL 8.0                   │
└─────────────────────────────────────────────┘
```

### 2.2 后端分层
```
controller/    → 接收HTTP请求、参数校验、返回响应
service/       → 业务逻辑层（接口 + impl实现）
mapper/        → 数据访问层（MyBatis-Plus BaseMapper）
entity/        → 数据库实体映射
dto/           → 数据传输对象（请求参数封装）
vo/            → 视图对象（响应数据封装）
common/        → 通用类（Result、异常处理、分页）
config/        → 配置类（CORS、MyBatis-Plus）
```

### 2.3 前端分层
```
views/         → 页面视图（按模块划分: book/ borrow/ purchase/ order/ user/）
components/    → 可复用组件
router/        → 路由配置
store/         → Pinia 状态管理
api/           → API 请求封装
utils/         → 工具函数（Axios实例等）
layouts/       → 布局组件
```

---

## 三、数据库设计

### 3.1 E-R 图（核心实体关系）

```
User ───< BorrowRecord >─── Book
  │                           │
  └──< Order >──< OrderItem >─┘

Category ───< Book
```

### 3.2 核心表

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `tb_user` | 用户表 | username, password, role, max_borrow_count |
| `tb_book` | 图书表 | isbn, title, sale_price, borrow_mode, available_stock |
| `tb_category` | 分类表 | name, parent_id（支持两级分类） |
| `tb_borrow_record` | 借阅记录 | borrow_no, user_id, book_id, status, due_date |
| `tb_order` | 订单表 | order_no, total_amount, payment_status, order_status |
| `tb_order_item` | 订单项 | order_id, book_id, quantity, subtotal |

> 完整建表语句见 `backend/src/main/resources/db/schema.sql`

### 3.3 借阅状态机

```
        借阅
  ┌──────────────┐
  │  BORROWING   │──── 归还 ────→ RETURNED
  │   (借阅中)    │
  └──────┬───────┘──── 逾期 ────→ OVERDUE
         │
         └── 续借（最多2次，每次+15天）
```

### 3.4 订单状态机

```
PENDING ──支付──→ PROCESSING ──发货──→ SHIPPED ──签收──→ DELIVERED
  │                    │
  └──取消──→ CANCELLED  │
                        └──退款──→ REFUNDED
```

---

## 四、接口设计概要

| 模块 | 前缀 | 核心接口 |
|------|------|----------|
| 用户 | `/api/user` | POST `/register`, POST `/login`, GET `/{id}`, PUT `/{id}` |
| 图书 | `/api/book` | POST `/`, GET `/search`, GET `/{id}`, PUT `/{id}`, DELETE `/{id}` |
| 借阅 | `/api/borrow` | POST `/`（借阅）, PUT `/return/{id}`, PUT `/renew/{id}`, GET `/user/{id}` |
| 订单 | `/api/order` | POST `/`（创建）, PUT `/pay/{id}`, PUT `/cancel/{id}`, PUT `/ship/{id}`, GET `/user/{id}` |

> 完整接口文档见 `docs/api-documentation.md`

---

## 五、非功能需求

| 指标 | 要求 |
|------|------|
| 响应时间 | 核心接口 < 500ms |
| 并发支持 | 初期支持 100 并发用户 |
| 数据安全 | 密码 MD5 加密，后期升级 BCrypt；SQL 防注入（MyBatis-Plus 参数化） |
| 代码规范 | 阿里巴巴 Java 开发手册；Vue 官方风格指南 |

---

## 六、项目结构总览

```
book/
├── frontend/                 # Vue 3 前端项目
│   ├── src/
│   │   ├── api/              # [组员] API 请求模块
│   │   ├── components/       # [组员] 公共组件
│   │   ├── router/           # [组长] 路由配置 ✅
│   │   ├── store/            # [组员] Pinia 状态管理
│   │   ├── utils/            # [组长] Axios 封装 ✅
│   │   └── views/            # [组员] 各页面视图
│   ├── package.json          # [组长] ✅
│   └── vite.config.js        # [组长] ✅
├── backend/                  # Spring Boot 后端项目
│   ├── src/main/java/com/bookSystem/
│   │   ├── common/           # [组长] 通用类 ✅
│   │   ├── config/           # [组长] 配置类 ✅
│   │   ├── controller/       # [组员] 控制器（已有模板）
│   │   ├── entity/           # [组长] 实体类 ✅
│   │   ├── mapper/           # [组员] 数据访问层
│   │   └── service/          # [组员] 业务逻辑层
│   ├── src/main/resources/
│   │   ├── application.yml   # [组长] ✅
│   │   └── db/schema.sql     # [组长] ✅
│   └── pom.xml               # [组长] ✅
└── docs/                     # [组长] 项目文档 ✅
    ├── system-design.md      # 系统设计文档（本文件）
    ├── api-documentation.md  # API 接口规范
    ├── test-plan.md          # 测试计划
    └── meeting-notes/        # 会议记录
```
