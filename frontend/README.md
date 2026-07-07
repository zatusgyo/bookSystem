# 前端开发

## 所用技术
- Vue 3.4+
- Element Plus 2.7+
- Vue Router 4.3+
- Pinia 2.1+
- Axios 1.7+
- Vite 5.2+

## 项目结构
```
src/
├── App.vue          # 根组件
├── main.js          # 入口文件
├── router/index.js  # 路由配置（已配置基础路由）
├── utils/request.js # Axios 封装（已配置拦截器）
├── views/           # 页面视图
│   ├── Home.vue         # [待实现] 首页
│   ├── Login.vue        # [待实现] 登录注册
│   ├── book/            # [待实现] 图书浏览/详情
│   ├── borrow/          # [待实现] 借阅管理
│   ├── purchase/        # [待实现] 购物车
│   ├── order/           # [待实现] 订单管理
│   └── user/            # [待实现] 个人中心
├── components/      # [待创建] 公共组件
├── store/           # [待创建] Pinia 状态管理
├── api/             # [待创建] API 请求模块
└── layouts/         # [待创建] 布局组件
```

## 待完成任务
1. [ ] 实现各页面视图（参考 views/ 下的 TODO 注释）
2. [ ] 封装 API 请求模块（api/ 目录）
3. [ ] 创建 Pinia store（用户状态、购物车状态等）
4. [ ] 设计公共组件（导航栏、图书卡片、分页等）
5. [ ] 创建布局组件（前台布局、后台布局）
6. [ ] 前后端联调

## 启动方式
```bash
cd frontend
npm install
npm run dev
```

## 页面路由对照
| 路径 | 页面 | 说明 |
|------|------|------|
| `/` | Home | 首页 |
| `/login` | Login | 登录/注册 |
| `/books` | BookList | 图书搜索与浏览 |
| `/books/:id` | BookDetail | 图书详情 |
| `/borrow` | BorrowList | 我的借阅 |
| `/cart` | Cart | 购物车 |
| `/orders` | OrderList | 我的订单 |
| `/user` | UserCenter | 个人中心 |
