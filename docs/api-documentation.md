# API 接口规范文档

> **版本**: v1.0 | **基准路径**: `http://localhost:8080/api` | **文档地址**: `http://localhost:8080/doc.html`

---

## 通用说明

### 统一响应格式

```json
{
  "code": 200,        // 状态码: 200-成功, 400-参数错误, 401-未授权, 404-不存在, 500-服务端错误
  "message": "操作成功",
  "data": {}           // 响应数据
}
```

### 分页响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 100,      // 总记录数
    "page": 1,         // 当前页码
    "size": 10,        // 每页大小
    "records": []      // 数据列表
  }
}
```

---

## 一、用户模块 `/api/user`

### 1.1 用户注册
```
POST /api/user/register
```
**请求体**:
```json
{
  "username": "zhangsan",
  "password": "123456",
  "nickname": "张三",
  "email": "zhangsan@example.com",
  "phone": "13800138000"
}
```
**响应**: 返回用户信息（不含密码）

---

### 1.2 用户登录
```
POST /api/user/login
```
**参数**: `username`, `password`（query参数）
**响应**: 返回用户信息

---

### 1.3 获取用户信息
```
GET /api/user/{id}
```

### 1.4 更新用户信息
```
PUT /api/user/{id}
```

---

## 二、图书模块 `/api/book`

### 2.1 添加图书（管理员）
```
POST /api/book
```
**请求体**:
```json
{
  "isbn": "978-7-111-11111-1",
  "title": "Java编程思想",
  "author": "Bruce Eckel",
  "publisher": "机械工业出版社",
  "categoryId": 9,
  "description": "Java经典入门书籍",
  "totalStock": 50,
  "salePrice": 79.00,
  "borrowMode": "SINGLE"
}
```

### 2.2 搜索图书
```
GET /api/book/search?keyword=Java&categoryId=9&page=1&size=10
```

### 2.3 图书详情
```
GET /api/book/{id}
```

### 2.4 更新图书
```
PUT /api/book/{id}
```

### 2.5 下架图书
```
DELETE /api/book/{id}
```

---

## 三、借阅模块 `/api/borrow`

### 3.1 借阅图书
```
POST /api/borrow?userId=1&bookId=1&borrowMode=SINGLE
```
- `borrowMode`: `SINGLE`（单人）/ `MULTI`（多人共读）

### 3.2 归还图书
```
PUT /api/borrow/return/{recordId}
```

### 3.3 续借图书
```
PUT /api/borrow/renew/{recordId}
```
- 每次续借 +15 天，最多续借 2 次

### 3.4 查询用户借阅记录
```
GET /api/borrow/user/{userId}?page=1&size=10
```

---

## 四、订单模块 `/api/order`

### 4.1 创建订单
```
POST /api/order
```
**请求体**:
```json
{
  "userId": 1,
  "bookIds": [1, 2],
  "quantities": [1, 2],
  "receiverName": "张三",
  "receiverPhone": "13800138000",
  "receiverAddress": "北京市朝阳区XX路XX号"
}
```

### 4.2 支付订单
```
PUT /api/order/pay/{orderId}?paymentMethod=ALIPAY
```
- `paymentMethod`: `ALIPAY`（支付宝）/ `WECHAT`（微信支付）

### 4.3 取消订单
```
PUT /api/order/cancel/{orderId}
```

### 4.4 发货（管理员）
```
PUT /api/order/ship/{orderId}
```

### 4.5 查询用户订单
```
GET /api/order/user/{userId}?page=1&size=10
```

### 4.6 订单详情
```
GET /api/order/{orderId}
```

---

## 五、状态码说明

| 状态码 | 含义 |
|--------|------|
| 200 | 操作成功 |
| 400 | 参数校验失败 |
| 401 | 未登录 / Token 过期 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 六、待办事项（组员实现）

- [ ] 添加 JWT Token 认证机制（目前接口无鉴权）
- [ ] 接入支付宝/微信支付 SDK
- [ ] 物流轨迹查询接口
- [ ] 图书评论/评分接口
- [ ] 管理员后台接口（用户管理、图书管理、数据统计）
