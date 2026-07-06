# 支付模块 — 学生项目模拟方案

> 本文档解释为什么我们使用模拟支付、如何工作、以及如何升级为真实支付。

---

## 一、为什么用模拟支付？

| 支付平台 | 接入条件 | 学生是否满足 |
|----------|----------|-------------|
| **支付宝** | 企业营业执照 + 对公账户 | ❌ |
| **微信支付** | 企业资质 + 商户号申请 | ❌ |
| **支付宝沙箱** | 个人可申请，但功能受限、不稳定 | ⚠️ 可选 |
| **模拟支付** | 无任何条件 | ✅ |

**结论**：学生项目采用**模拟支付**是最务实的选择。

---

## 二、模拟方案设计

### 2.1 设计原则

- **接口规范**：`PaymentService` 接口与真实支付 SDK 调用模式完全一致
- **流程完整**：保留"选择支付方式 → 生成交易流水 → 支付结果"的完整链路
- **数据可查**：每次支付写入 `tb_payment_record` 表，有据可查
- **可升级**：后续只需新建实现类，无需改动订单业务代码

### 2.2 核心代码

```
OrderServiceImpl.payOrder()
    │
    ├── 1. 校验订单状态（必须 UNPAID）
    │
    ├── 2. 调用 PaymentService.pay(order, method)
    │       │
    │       └── MockPaymentServiceImpl
    │           ├── 生成交易流水号: ALIPAY20240101120000123456
    │           ├── 写入 tb_payment_record
    │           ├── 95% 概率返回 SUCCESS
    │           └── 5% 概率返回 FAILED（模拟网络波动）
    │
    ├── 3. 更新订单: paymentTradeNo, PAID, PROCESSING
    │
    └── 4. 返回支付结果
```

### 2.3 交易流水号格式

| 前缀 | 含义 | 示例 |
|------|------|------|
| `ALIPAY` | 模拟支付宝 | `ALIPAY20240106143000234567` |
| `WECHAT` | 模拟微信支付 | `WECHAT20240106143000765432` |

格式：`{支付方式}{yyyyMMddHHmmss}{6位随机数}`

> 真实接入后，替换为支付宝返回的 `trade_no` 或微信返回的 `transaction_id`。

### 2.4 支付记录表

```sql
tb_payment_record
├── trade_no      交易流水号（唯一）
├── order_id      订单ID
├── order_no      订单编号（冗余）
├── amount        支付金额
├── payment_method  ALIPAY / WECHAT
├── status        PROCESSING / SUCCESS / FAILED
├── create_time   发起时间
└── complete_time 完成时间
```

---

## 三、答辩演示要点

### 演示流程
1. 用户浏览图书 → 加入购物车 → 创建订单
2. 点击"去支付" → 选择**支付宝**或**微信支付**
3. 系统生成交易流水号 → 显示"支付处理中..."
4. 返回支付结果（成功/失败）

### 答辩话术参考
> "由于支付宝和微信支付需要企业资质才能申请商户号，作为学生项目我们采用了**模拟支付方案**。
> 但我们设计了规范的 `PaymentService` 接口，模拟了完整的交易流水记录。
> 如果未来需要真实接入，只需新建一个 `AlipayServiceImpl` 实现同一接口即可，**
> 业务代码不需要任何改动**，体现了良好的**开闭原则**设计。"

---

## 四、升级为真实支付（仅需3步）

### 步骤1: 引入SDK依赖

取消 `pom.xml` 中的注释：
```xml
<!-- 支付宝 SDK -->
<dependency>
    <groupId>com.alipay.sdk</groupId>
    <artifactId>alipay-sdk-java</artifactId>
    <version>4.38.157.ALL</version>
</dependency>
```

### 步骤2: 新建实现类

```java
@Service
public class AlipayServiceImpl implements PaymentService {

    @Override
    public PaymentRecord pay(Order order, String paymentMethod) {
        // 1. 调用支付宝 SDK 生成预支付订单
        // AlipayClient alipayClient = new DefaultAlipayClient(...);
        // AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        // ...

        // 2. 保存交易记录
        // ...

        // 3. 返回支付记录（含真实 transaction_id）
        return paymentRecord;
    }
}
```

### 步骤3: 添加支付回调

```java
@RestController
@RequestMapping("/api/payment/callback")
public class PaymentCallbackController {

    @PostMapping("/alipay")
    public String alipayNotify(HttpServletRequest request) {
        // 处理支付宝异步通知
        // 验签 → 更新订单状态
        return "success";
    }
}
```

> `PaymentService` 接口保持不变，`OrderServiceImpl` 零改动 — 这就是接口设计的价值。

---

## 五、相关文件索引

| 文件 | 说明 |
|------|------|
| `backend/.../service/PaymentService.java` | 支付接口（规范） |
| `backend/.../service/impl/MockPaymentServiceImpl.java` | 模拟实现 |
| `backend/.../entity/PaymentRecord.java` | 支付流水实体 |
| `backend/.../mapper/PaymentRecordMapper.java` | 支付流水Mapper |
| `backend/.../resources/db/schema.sql` | `tb_payment_record` 建表语句 |
| `docs/system-design.md` §四 | 支付模块架构设计 |
| `docs/api-documentation.md` §4.2-4.3 | 支付接口文档 |
