<template>
  <div class="order-list">
    <h2>我的订单</h2>

    <div v-loading="loading">
      <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />

      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <span class="order-no">订单号: {{ order.orderNo }}</span>
          <span class="order-time">{{ order.createTime }}</span>
          <el-tag :type="statusTag(order.orderStatus)">{{ order.orderStatus }}</el-tag>
          <el-tag :type="payTag(order.paymentStatus)" style="margin-left: 8px">
            {{ order.paymentStatus === 'PAID' ? '已支付' : order.paymentStatus === 'UNPAID' ? '待支付' : '已退款' }}
          </el-tag>
        </div>
        <div class="order-body">
          <div class="order-info">
            <span>收货人: {{ order.receiverName }}</span>
            <span>手机: {{ order.receiverPhone }}</span>
            <span>地址: {{ order.receiverAddress }}</span>
            <span v-if="order.paymentMethod">支付方式: {{ order.paymentMethod === 'ALIPAY' ? '支付宝' : '微信支付' }}</span>
          </div>
          <div class="order-amount">
            <span class="total-label">订单金额</span>
            <span class="total-price">¥{{ order.totalAmount }}</span>
          </div>
        </div>
        <div class="order-actions">
          <el-button
            v-if="order.paymentStatus === 'UNPAID'"
            type="primary"
            size="small"
            @click="handlePay(order)"
            :loading="payingId === order.id"
          >
            去支付
          </el-button>
          <el-button
            v-if="order.paymentStatus === 'UNPAID'"
            type="danger"
            size="small"
            plain
            @click="handleCancel(order)"
          >
            取消订单
          </el-button>
          <el-button
            v-if="order.orderStatus === 'SHIPPED' || order.orderStatus === 'DELIVERED'"
            type="primary"
            size="small"
            plain
            @click="showTracking(order)"
          >
            查看物流
          </el-button>
          <el-tag v-if="order.orderStatus === 'SHIPPED'" type="success">已发货</el-tag>
          <el-tag v-if="order.orderStatus === 'DELIVERED'" type="success">已签收</el-tag>
        </div>
      </div>

      <div class="pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          :page-size="size"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchOrders"
        />
      </div>
    </div>

    <!-- 物流轨迹弹窗 -->
    <el-dialog v-model="trackingVisible" title="物流轨迹" width="550px">
      <div v-loading="trackingLoading">
        <el-empty v-if="!trackingLoading && tracks.length === 0" description="暂无物流信息" />
        <el-timeline v-else>
          <el-timeline-item
            v-for="(track, index) in tracks"
            :key="track.id"
            :timestamp="track.trackTime"
            :color="index === 0 ? '#0bbd87' : '#bbb'"
          >
            <p class="track-location">{{ track.location }}</p>
            <p class="track-desc">{{ track.description }}</p>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserOrders, cancelOrder } from '@/api/order'
import { initiatePayment } from '@/api/payment'
import { getOrderTracks } from '@/api/shipping'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

const loading = ref(false)
const payingId = ref(null)
const orders = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

// 物流相关
const trackingVisible = ref(false)
const trackingLoading = ref(false)
const tracks = ref([])

onMounted(() => {
  fetchOrders()
})

async function fetchOrders() {
  loading.value = true
  try {
    const res = await getUserOrders(userStore.user.id, page.value, size.value)
    if (res.data) {
      orders.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    orders.value = []
  } finally {
    loading.value = false
  }
}

async function handlePay(order) {
  payingId.value = order.id
  try {
    const res = await initiatePayment(order.id, 'ALIPAY')
    const { tradeNo, orderNo, amount, paymentMethod, orderId } = res.data
    router.push({
      path: '/pay',
      query: { tradeNo, orderNo, amount, paymentMethod, orderId }
    })
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    payingId.value = null
  }
}

async function handleCancel(order) {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '确认取消', { type: 'warning' })
    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (e) {
    // 取消操作
  }
}

async function showTracking(order) {
  trackingVisible.value = true
  trackingLoading.value = true
  try {
    const res = await getOrderTracks(order.id)
    tracks.value = res.data || []
  } catch (e) {
    tracks.value = []
  } finally {
    trackingLoading.value = false
  }
}

function statusTag(status) {
  const map = { PENDING: 'warning', PROCESSING: 'primary', SHIPPED: 'success', DELIVERED: 'success', CANCELLED: 'info' }
  return map[status] || 'info'
}

function payTag(status) {
  const map = { UNPAID: 'warning', PAID: 'success', REFUNDING: 'warning', REFUNDED: 'info' }
  return map[status] || 'info'
}
</script>

<style scoped>
.order-list h2 {
  font-size: 22px;
  margin-bottom: 20px;
}

.order-card {
  background: #fff;
  border-radius: 8px;
  margin-bottom: 16px;
  padding: 16px 20px;
}

.order-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-no {
  font-size: 14px;
  color: #333;
  font-weight: 600;
}

.order-time {
  font-size: 13px;
  color: #999;
  flex: 1;
}

.order-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
}

.order-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 13px;
  color: #666;
}

.order-amount {
  text-align: right;
}

.total-label {
  font-size: 13px;
  color: #999;
  margin-right: 8px;
}

.total-price {
  font-size: 20px;
  color: #e33;
  font-weight: bold;
}

.order-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.track-location {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.track-desc {
  font-size: 13px;
  color: #666;
  margin: 0;
}
</style>