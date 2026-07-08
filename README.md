# 图书借阅与销售平台 — BookSystem

> 2026年小组实验项目 | 4人团队

---

## 👥 团队分工

| 角色 | 姓名 | 职责 |
|------|------|------|
| **组长** | [你的名字] | 项目管理、系统设计、数据库设计、测试与文档、代码审核 |
| **后端开发** | 组员A | Spring Boot 业务逻辑实现、Service/Mapper/Controller 开发 |
| **后端开发** | 组员B | 支付模块、物流模块、安全认证模块 |
| **前端开发** | 组员C | Vue 页面开发、组件封装、前后端联调 |

---

## 🚀 快速启动

### 环境要求
- **JDK** 1.8+
- **Maven** 3.6+
- **Node.js** 18+
- **MySQL** 8.0+

### 1. 初始化数据库
```bash
mysql -u root -p < backend/src/main/resources/db/schema.sql
```

### 2. 启动后端
```bash
cd backend
mvn spring-boot:run
# 启动后访问 API 文档: http://localhost:8080/doc.html
```

### 3. 启动前端
```bash
cd frontend
npm install
npm run dev
# 启动后访问: http://localhost:3000
```

---

## 📁 项目结构

```
book/
├── frontend/                 # Vue 3 前端
│   └── src/
│       ├── api/              # API 请求模块 (admin/book/borrow/comment/order/payment/shipping/user)
│       ├── components/       # 公共组件
│       ├── layouts/          # 布局组件 (Layout/MainLayout/NavBar)
│       ├── router/           # 路由配置
│       ├── store/            # Pinia 状态管理 (user/cart)
│       ├── utils/            # 工具函数 (Axios封装)
│       └── views/            # 页面 (Home/Login/admin/book/borrow/order/purchase/user)
├── backend/                  # Spring Boot 后端
│   └── src/main/java/com/bookSystem/
│       ├── common/           # 通用类 (Result/PageResult/异常处理)
│       ├── config/           # 配置 (CORS/JWT拦截器/MyBatis-Plus)
│       ├── controller/       # 控制器 (9个: Admin/Book/Borrow/Category/Comment/Order/Payment/Shipping/User)
│       ├── dto/              # 请求DTO (7个: Login/Register/BookSearch/BorrowBook/CreateOrder/AddComment/UpdateUser)
│       ├── entity/           # 实体类 (10个)
│       ├── mapper/           # MyBatis-Plus Mapper
│       ├── service/          # 业务接口 + impl 实现
│       ├── utils/            # 工具类 (JwtUtils)
│       └── vo/               # 响应VO (7个: Book/BorrowRecord/Category/Comment/OrderItem/Order/User)
└── docs/                     # 文档
    ├── system-design.md      # 系统设计文档
    ├── api-documentation.md  # API 接口规范
    ├── test-plan.md          # 测试计划
    ├── payment-mock-guide.md # 模拟支付说明
    └── meeting-notes/        # 会议记录
```

---

## 📋 开发规范

### Git 分支策略
```
main          ← 主分支，稳定版本
├── dev       ← 开发分支，日常合并
│   ├── feature/user    ← 用户模块
│   ├── feature/book    ← 图书模块
│   ├── feature/borrow  ← 借阅模块
│   ├── feature/order   ← 订单模块
│   └── feature/front   ← 前端页面
└── release   ← 发布分支
```

### 代码提交规范
```
feat: 新功能
fix: Bug 修复
docs: 文档更新
style: 代码格式（不影响功能）
refactor: 重构
test: 测试相关
chore: 构建/工具相关
```

### 后端开发规范
- 遵循阿里巴巴 Java 开发手册
- Controller 只做参数校验和路由，业务逻辑放在 Service
- 使用 Lombok 简化代码
- 统一使用 `Result<T>` 封装响应

### 前端开发规范
- 组件命名：PascalCase（如 `BookCard.vue`）
- 页面命名：PascalCase（如 `BookList.vue`）
- API 请求统一使用 `utils/request.js` 中的 Axios 实例
- 使用 Element Plus 作为 UI 组件库

---

## 🔗 相关链接

- 远程仓库: https://github.com/zatusgyo/bookSystem.git
- API 文档: http://localhost:8080/doc.html
- 前端页面: http://localhost:3000

---

## 📝 实验进度

| 阶段 | 计划时间 | 状态 | 负责人 |
|------|----------|------|--------|
| 需求分析 + 系统设计 | 第1周 | ✅ 已完成 | 组长 |
| 数据库设计 + 建表 | 第1周 | ✅ 已完成 | 组长 |
| 后端框架搭建 | 第2周 | ✅ 已完成 | 组长 |
| 前端框架搭建 | 第2周 | ✅ 已完成 | 组长 |
| 后端业务开发 | 第3-4周 | ✅ 已完成 | 组员A、组员B |
| 前端页面开发 | 第3-4周 | ✅ 已完成 | 组员C |
| 前后端联调 | 第5周 | 🔲 待开始 | 全组 |
| 测试 + Bug修复 | 第6周 | 🔲 待开始 | 组长 |
| 文档完善 + 答辩准备 | 第7周 | 🔲 待开始 | 全组 |

---

## ✅ 已完成功能清单

### 后端 (Spring Boot)
| 模块 | 功能 | 开发者 |
|------|------|--------|
| 🔐 用户认证 | 注册/登录/修改密码、JWT Token、角色鉴权 | 组员B |
| 📚 图书管理 | 图书CRUD、搜索（关键词/分类）、上架下架 | 组员A |
| 🌲 分类管理 | 树形分类结构、父子分类CRUD | 组员A |
| 📖 借阅管理 | 借阅/归还/续借、逾期罚款、多人共读模式 | 组员A |
| 🛒 订单管理 | 创建订单/支付/取消/发货、订单项管理 | 组员A |
| 💳 支付模块 | 模拟支付宝/微信支付、交易流水记录 | 组员B |
| 🚚 物流跟踪 | 发货自动生成轨迹、轨迹查询 | 组员B |
| 💬 评论评分 | 图书评论、1-5星评分 | 组员A |
| 🎛️ 管理后台 | 数据统计、用户/图书/订单/借阅管理 | 组员A |
| 📐 分层架构 | DTO/VO 参数校验与数据脱敏 | 组员A |

### 前端 (Vue 3)
| 页面 | 功能 | 开发者 |
|------|------|--------|
| 🏠 首页 | 图书推荐/搜索入口 | 组员C |
| 📋 图书列表 | 分类筛选、关键词搜索、分页 | 组员C |
| 📖 图书详情 | 详情展示、借阅/购买入口、评论评分 | 组员C |
| 📚 借阅管理 | 借阅记录、归还/续借操作 | 组员C |
| 🛒 购物车 | 加入购物车、数量修改、结算 | 组员C |
| 💳 模拟支付 | 支付方式选择、倒计时确认 | 组员C |
| 📦 订单管理 | 订单列表、支付/取消、物流查看 | 组员C |
| 👤 个人中心 | 个人信息修改、密码修改 | 组员C |
| 🔑 登录注册 | 登录/注册表单、Token存储 | 组员C |
| 🎛️ 管理后台 | 数据面板、用户/图书/订单管理 | 组员C |

### 公共组件 & 状态管理
| 组件 | 说明 | 开发者 |
|------|------|--------|
| Layout / MainLayout / NavBar | 页面布局与导航栏 | 组员C |
| Pinia userStore | 用户状态管理 | 组员C |
| Pinia cartStore | 购物车状态管理 | 组员C |
| Axios 封装 | 请求/响应拦截器 | 组长 |
