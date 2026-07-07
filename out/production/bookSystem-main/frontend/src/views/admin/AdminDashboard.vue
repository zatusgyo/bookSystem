<template>
  <div class="admin-dashboard">
    <h2>管理员后台</h2>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-value">{{ stats.userCount }}</div>
        <div class="stat-label">用户总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.bookCount }}</div>
        <div class="stat-label">图书总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.borrowCount }}</div>
        <div class="stat-label">借阅记录</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.orderCount }}</div>
        <div class="stat-label">订单总数</div>
      </div>
    </div>

    <!-- 管理 Tab -->
    <el-tabs v-model="activeTab" class="admin-tabs">
      <!-- 用户管理 -->
      <el-tab-pane label="用户管理" name="users">
        <el-table :data="users" v-loading="userLoading" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="nickname" label="昵称" />
          <el-table-column prop="email" label="邮箱" />
          <el-table-column prop="phone" label="手机号" />
          <el-table-column prop="role" label="角色" width="100">
            <template #default="{ row }">
              <el-tag :type="row.role === 'ADMIN' ? 'danger' : row.role === 'VIP' ? 'warning' : 'primary'" size="small">
                {{ row.role === 'ADMIN' ? '管理员' : row.role === 'VIP' ? 'VIP' : '普通' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
                {{ row.status === 0 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button
                :type="row.status === 0 ? 'danger' : 'success'"
                size="small"
                @click="toggleUserStatus(row)"
              >
                {{ row.status === 0 ? '禁用' : '启用' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          v-model:current-page="userPage"
          :page-size="10"
          :total="userTotal"
          layout="prev, pager, next"
          style="margin-top:16px;justify-content:center"
          @current-change="fetchUsers"
        />
      </el-tab-pane>

      <!-- 图书管理 -->
      <el-tab-pane label="图书管理" name="books">
        <el-table :data="books" v-loading="bookLoading" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="书名" min-width="150" />
          <el-table-column prop="author" label="作者" width="120" />
          <el-table-column prop="isbn" label="ISBN" width="140" />
          <el-table-column prop="salePrice" label="售价" width="80" />
          <el-table-column prop="availableStock" label="可借" width="60" />
          <el-table-column prop="totalStock" label="库存" width="60" />
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                {{ row.status === 1 ? '上架' : '下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button
                :type="row.status === 1 ? 'danger' : 'success'"
                size="small"
                @click="toggleBookStatus(row)"
              >
                {{ row.status === 1 ? '下架' : '上架' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          v-model:current-page="bookPage"
          :page-size="10"
          :total="bookTotal"
          layout="prev, pager, next"
          style="margin-top:16px;justify-content:center"
          @current-change="fetchBooks"
        />
      </el-tab-pane>

      <!-- 借阅管理 -->
      <el-tab-pane label="借阅管理" name="borrows">
        <el-table :data="borrows" v-loading="borrowLoading" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="borrowNo" label="借阅编号" width="150" />
          <el-table-column prop="userId" label="用户ID" width="80" />
          <el-table-column prop="bookId" label="图书ID" width="80" />
          <el-table-column prop="borrowMode" label="借阅模式" width="100">
            <template #default="{ row }">
              {{ row.borrowMode === 'MULTI' ? '多人共读' : '单人借阅' }}
            </template>
          </el-table-column>
          <el-table-column prop="borrowDate" label="借阅日期" width="110" />
          <el-table-column prop="dueDate" label="应还日期" width="110" />
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 'BORROWING' ? 'primary' : row.status === 'OVERDUE' ? 'danger' : 'success'" size="small">
                {{ row.status === 'BORROWING' ? '借阅中' : row.status === 'OVERDUE' ? '逾期' : '已还' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          v-model:current-page="borrowPage"
          :page-size="10"
          :total="borrowTotal"
          layout="prev, pager, next"
          style="margin-top:16px;justify-content:center"
          @current-change="fetchBorrows"
        />
      </el-tab-pane>

      <!-- 订单管理 -->
      <el-tab-pane label="订单管理" name="orders">
        <el-table :data="orders" v-loading="orderLoading" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="orderNo" label="订单号" width="150" />
          <el-table-column prop="userId" label="用户ID" width="80" />
          <el-table-column prop="totalAmount" label="金额" width="80" />
          <el-table-column prop="paymentStatus" label="支付" width="80">
            <template #default="{ row }">
              <el-tag :type="row.paymentStatus === 'PAID' ? 'success' : 'warning'" size="small">
                {{ row.paymentStatus === 'PAID' ? '已支付' : '待支付' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="orderStatus" label="订单状态" width="90">
            <template #default="{ row }">
              <el-tag :type="statusTag(row.orderStatus)" size="small">{{ row.orderStatus }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="receiverName" label="收货人" width="80" />
          <el-table-column prop="receiverPhone" label="手机" width="120" />
          <el-table-column prop="createTime" label="时间" width="170" />
        </el-table>
        <el-pagination
          v-model:current-page="orderPage"
          :page-size="10"
          :total="orderTotal"
          layout="prev, pager, next"
          style="margin-top:16px;justify-content:center"
          @current-change="fetchOrders"
        />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { getStats, getUserList, updateUserStatus, getBookList, updateBookStatus, getBorrowList, getOrderList } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('users')

const stats = ref({ userCount: 0, bookCount: 0, borrowCount: 0, orderCount: 0 })

// 用户管理
const userLoading = ref(false)
const users = ref([])
const userPage = ref(1)
const userTotal = ref(0)

// 图书管理
const bookLoading = ref(false)
const books = ref([])
const bookPage = ref(1)
const bookTotal = ref(0)

// 借阅管理
const borrowLoading = ref(false)
const borrows = ref([])
const borrowPage = ref(1)
const borrowTotal = ref(0)

// 订单管理
const orderLoading = ref(false)
const orders = ref([])
const orderPage = ref(1)
const orderTotal = ref(0)

onMounted(() => {
  fetchStats()
  fetchUsers()
})

watch(activeTab, (tab) => {
  if (tab === 'books') fetchBooks()
  if (tab === 'borrows') fetchBorrows()
  if (tab === 'orders') fetchOrders()
})

async function fetchStats() {
  try {
    const res = await getStats()
    stats.value = res.data
  } catch (e) { /* ignore */ }
}

async function fetchUsers() {
  userLoading.value = true
  try {
    const res = await getUserList(userPage.value)
    users.value = res.data?.records || []
    userTotal.value = res.data?.total || 0
  } catch (e) { users.value = [] }
  finally { userLoading.value = false }
}

async function toggleUserStatus(row) {
  const newStatus = row.status === 0 ? 1 : 0
  const action = newStatus === 1 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定${action}该用户？`, '确认操作', { type: 'warning' })
    await updateUserStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    fetchUsers()
  } catch (e) { /* cancel */ }
}

async function fetchBooks() {
  bookLoading.value = true
  try {
    const res = await getBookList(bookPage.value)
    books.value = res.data?.records || []
    bookTotal.value = res.data?.total || 0
  } catch (e) { books.value = [] }
  finally { bookLoading.value = false }
}

async function toggleBookStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '上架' : '下架'
  try {
    await ElMessageBox.confirm(`确定${action}该图书？`, '确认操作', { type: 'warning' })
    await updateBookStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    fetchBooks()
  } catch (e) { /* cancel */ }
}

async function fetchBorrows() {
  borrowLoading.value = true
  try {
    const res = await getBorrowList(borrowPage.value)
    borrows.value = res.data?.records || []
    borrowTotal.value = res.data?.total || 0
  } catch (e) { borrows.value = [] }
  finally { borrowLoading.value = false }
}

async function fetchOrders() {
  orderLoading.value = true
  try {
    const res = await getOrderList(orderPage.value)
    orders.value = res.data?.records || []
    orderTotal.value = res.data?.total || 0
  } catch (e) { orders.value = [] }
  finally { orderLoading.value = false }
}

function statusTag(status) {
  const map = { PENDING: 'warning', PROCESSING: 'primary', SHIPPED: 'success', DELIVERED: 'success', CANCELLED: 'info' }
  return map[status] || 'info'
}
</script>

<style scoped>
.admin-dashboard h2 {
  font-size: 22px;
  margin-bottom: 20px;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  text-align: center;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 8px;
}

.admin-tabs {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}
</style>