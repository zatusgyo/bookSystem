<template>
  <div class="mock-pay">
    <div class="pay-card">
      <!-- 支付中 -->
      <template v-if="payStatus === 'paying'">
        <h2>模拟支付</h2>
        <p class="pay-amount">
          <span class="currency">¥</span>
          <span class="number">{{ amount }}</span>
        </p>
        <div class="pay-info">
          <span>订单号：{{ orderNo }}</span>
          <span>支付方式：{{ methodName }}</span>
        </div>

        <!-- 模拟二维码 -->
        <div class="qr-box">
          <div class="qr-code">
            <svg viewBox="0 0 200 200" width="200" height="200">
              <!-- 模拟QR码图案 -->
              <rect x="20" y="20" width="50" height="50" rx="5" fill="#333" />
              <rect x="130" y="20" width="50" height="50" rx="5" fill="#333" />
              <rect x="20" y="130" width="50" height="50" rx="5" fill="#333" />
              <rect x="90" y="60" width="20" height="20" rx="3" fill="#333" />
              <rect x="60" y="90" width="20" height="20" rx="3" fill="#333" />
              <rect x="120" y="90" width="20" height="20" rx="3" fill="#333" />
              <rect x="90" y="120" width="20" height="20" rx="3" fill="#333" />
              <rect x="30" y="90" width="40" height="8" rx="4" fill="#333" />
              <rect x="130" y="90" width="40" height="8" rx="4" fill="#333" />
              <rect x="80" y="40" width="8" height="40" rx="4" fill="#333" />
              <rect x="80" y="120" width="40" height="8" rx="4" fill="#333" />
              <rect x="30" y="170" width="60" height="8" rx="4" fill="#333" />
              <rect x="110" y="170" width="60" height="8" rx="4" fill="#333" />
              <!-- 中心logo -->
              <rect x="85" y="85" width="30" height="30" rx="4" fill="#409eff" />
              <text x="100" y="105" text-anchor="middle" fill="white" font-size="14" font-weight="bold">支</text>
            </svg>
          </div>
          <div class="qr-scanning">
            <div class="scan-line"></div>
          </div>
        </div>

        <p class="pay-tip">请使用手机扫描二维码完成支付</p>

        <el-progress :percentage="progress" :stroke-width="6" :color="'#409eff'" />
        <p class="countdown-text">模拟支付中... {{ countdown }}s</p>
      </template>

      <!-- 支付成功 -->
      <template v-if="payStatus === 'success'">
        <div class="result-icon success">
          <svg viewBox="0 0 80 80" width="80" height="80">
            <circle cx="40" cy="40" r="36" fill="none" stroke="#67c23a" stroke-width="4" />
            <path d="M22 42 L34 54 L58 28" fill="none" stroke="#67c23a" stroke-width="5" stroke-linecap="round" stroke-linejoin="round" />
          </svg>
        </div>
        <h2 class="result-title">支付成功</h2>
        <p class="result-amount">¥{{ amount }}</p>
        <el-button type="primary" @click="goOrders">查看订单</el-button>
        <el-button @click="goHome">返回首页</el-button>
      </template>

      <!-- 支付失败 -->
      <template v-if="payStatus === 'failed'">
        <div class="result-icon fail">
          <svg viewBox="0 0 80 80" width="80" height="80">
            <circle cx="40" cy="40" r="36" fill="none" stroke="#f56c6c" stroke-width="4" />
            <path d="M28 28 L52 52 M52 28 L28 52" fill="none" stroke="#f56c6c" stroke-width="5" stroke-linecap="round" />
          </svg>
        </div>
        <h2 class="result-title">支付失败</h2>
        <p class="result-msg">{{ errorMsg }}</p>
        <el-button type="primary" @click="retryPay">重新支付</el-button>
        <el-button @click="goOrders">返回订单</el-button>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { confirmPayment } from '@/api/payment'
import { payOrder } from '@/api/order'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const orderNo = ref('')
const amount = ref('0.00')
const tradeNo = ref('')
const paymentMethod = ref('ALIPAY')
const orderId = ref('')

const payStatus = ref('paying') // paying | success | failed
const countdown = ref(3)
const progress = ref(0)
const errorMsg = ref('')
let timer = null

const methodName = computed(() => paymentMethod.value === 'ALIPAY' ? '支付宝' : '微信支付')

onMounted(() => {
  orderNo.value = route.query.orderNo || ''
  amount.value = route.query.amount || '0.00'
  tradeNo.value = route.query.tradeNo || ''
  paymentMethod.value = route.query.paymentMethod || 'ALIPAY'
  orderId.value = route.query.orderId || ''

  if (!tradeNo.value) {
    payStatus.value = 'failed'
    errorMsg.value = '缺少支付参数'
    return
  }

  startCountdown()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

function startCountdown() {
  const total = 3
  const interval = 100
  let elapsed = 0

  timer = setInterval(() => {
    elapsed += interval
    progress.value = Math.min(100, Math.round((elapsed / (total * 1000)) * 100))

    if (elapsed >= total * 1000) {
      clearInterval(timer)
      countdown.value = 0
      handleConfirmPay()
    } else {
      countdown.value = Math.ceil((total * 1000 - elapsed) / 1000)
    }
  }, interval)
}

async function handleConfirmPay() {
  try {
    // 1. 确认支付（模拟回调）
    await confirmPayment(tradeNo.value)

    // 2. 更新订单状态
    await payOrder(orderId.value, paymentMethod.value)

    payStatus.value = 'success'
  } catch (e) {
    payStatus.value = 'failed'
    errorMsg.value = '支付处理失败，请重试'
  }
}

function goOrders() {
  router.push('/orders')
}

function goHome() {
  router.push('/')
}

function retryPay() {
  payStatus.value = 'paying'
  countdown.value = 3
  progress.value = 0
  startCountdown()
}
</script>

<style scoped>
.mock-pay {
  display: flex;
  justify-content: center;
  padding-top: 40px;
}

.pay-card {
  background: #fff;
  border-radius: 16px;
  padding: 40px 60px;
  text-align: center;
  min-width: 420px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}

.pay-card h2 {
  font-size: 22px;
  color: #333;
  margin-bottom: 16px;
}

.pay-amount {
  margin-bottom: 16px;
}

.pay-amount .currency {
  font-size: 20px;
  color: #e33;
  vertical-align: top;
}

.pay-amount .number {
  font-size: 36px;
  font-weight: bold;
  color: #e33;
}

.pay-info {
  display: flex;
  justify-content: center;
  gap: 24px;
  font-size: 13px;
  color: #999;
  margin-bottom: 24px;
}

.qr-box {
  position: relative;
  display: inline-block;
  margin-bottom: 16px;
  border: 2px solid #eee;
  border-radius: 12px;
  padding: 12px;
  overflow: hidden;
}

.qr-code {
  position: relative;
  z-index: 1;
}

.qr-scanning {
  position: absolute;
  top: 12px;
  left: 12px;
  right: 12px;
  bottom: 12px;
  overflow: hidden;
}

.scan-line {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, #409eff, transparent);
  animation: scan 2s linear infinite;
}

@keyframes scan {
  from { top: 0; }
  to { top: 100%; }
}

.pay-tip {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

.countdown-text {
  font-size: 13px;
  color: #999;
  margin-top: 8px;
}

.result-icon {
  margin-bottom: 16px;
}

.result-title {
  font-size: 22px;
  margin-bottom: 8px;
}

.result-title { color: #333; }

.result-amount {
  font-size: 28px;
  font-weight: bold;
  color: #e33;
  margin-bottom: 24px;
}

.result-msg {
  font-size: 14px;
  color: #999;
  margin-bottom: 24px;
}

.result-icon + .result-title {
  margin-top: 8px;
}
</style>