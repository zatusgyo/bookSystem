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
│       ├── views/            # 页面 (book/ borrow/ purchase/ order/ user/)
│       ├── router/           # 路由
│       ├── store/            # 状态管理
│       ├── api/              # API 请求
│       ├── components/       # 公共组件
│       └── utils/            # 工具函数
├── backend/                  # Spring Boot 后端
│   └── src/main/java/com/bookSystem/
│       ├── controller/       # 控制器
│       ├── service/          # 业务逻辑
│       ├── mapper/           # 数据访问
│       ├── entity/           # 实体类
│       └── common/           # 通用类
└── docs/                     # 文档
    ├── system-design.md      # 系统设计文档
    ├── api-documentation.md  # API 接口规范
    └── test-plan.md          # 测试计划
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
| 后端业务开发 | 第3-4周 | 🔲 待开始 | 组员A/B |
| 前端页面开发 | 第3-4周 | 🔲 待开始 | 组员C |
| 前后端联调 | 第5周 | 🔲 待开始 | 全组 |
| 测试 + Bug修复 | 第6周 | 🔲 待开始 | 组长 |
| 文档完善 + 答辩准备 | 第7周 | 🔲 待开始 | 全组 |
