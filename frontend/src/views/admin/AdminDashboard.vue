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

    <el-tabs v-model="activeTab" class="admin-tabs">
      <!-- ==================== 用户管理 ==================== -->
      <el-tab-pane label="用户管理" name="users">
        <div class="tab-toolbar">
          <el-button type="primary" @click="openAddUser">添加用户</el-button>
        </div>
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
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" @click="openEditUser(row)">编辑</el-button>
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
          v-model:current-page="userPage" :page-size="10" :total="userTotal"
          layout="prev, pager, next" style="margin-top:16px;justify-content:center"
          @current-change="fetchUsers"
        />
      </el-tab-pane>

      <!-- ==================== 图书管理 ==================== -->
      <el-tab-pane label="图书管理" name="books">
        <div class="tab-toolbar">
          <el-button type="primary" @click="openAddBook">添加图书</el-button>
        </div>
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
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" @click="openEditBook(row)">编辑</el-button>
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
          v-model:current-page="bookPage" :page-size="10" :total="bookTotal"
          layout="prev, pager, next" style="margin-top:16px;justify-content:center"
          @current-change="fetchBooks"
        />
      </el-tab-pane>

      <!-- ==================== 借阅管理 ==================== -->
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
        <el-pagination v-model:current-page="borrowPage" :page-size="10" :total="borrowTotal"
          layout="prev, pager, next" style="margin-top:16px;justify-content:center" @current-change="fetchBorrows" />
      </el-tab-pane>

      <!-- ==================== 订单管理 ==================== -->
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
        <el-pagination v-model:current-page="orderPage" :page-size="10" :total="orderTotal"
          layout="prev, pager, next" style="margin-top:16px;justify-content:center" @current-change="fetchOrders" />
      </el-tab-pane>
    </el-tabs>

    <!-- ==================== 添加/编辑图书弹窗 ==================== -->
    <el-dialog v-model="bookDialogVisible" :title="bookDialogTitle" width="600px" @close="resetBookForm">
      <el-form :model="bookForm" :rules="bookRules" ref="bookFormRef" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="书名" prop="title">
              <el-input v-model="bookForm.title" placeholder="请输入书名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作者" prop="author">
              <el-input v-model="bookForm.author" placeholder="请输入作者" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="ISBN">
              <el-input v-model="bookForm.isbn" placeholder="请输入ISBN" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出版社">
              <el-input v-model="bookForm.publisher" placeholder="请输入出版社" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="分类ID" prop="categoryId">
              <el-input-number v-model="bookForm.categoryId" :min="1" :max="99" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="售价" prop="salePrice">
              <el-input-number v-model="bookForm.salePrice" :min="0" :precision="2" :step="1" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="总库存" prop="totalStock">
              <el-input-number v-model="bookForm.totalStock" :min="0" :step="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="借阅模式">
              <el-select v-model="bookForm.borrowMode" style="width:100%">
                <el-option label="单人借阅" value="SINGLE" />
                <el-option label="多人共读" value="MULTI" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="简介">
          <el-input v-model="bookForm.description" type="textarea" :rows="3" placeholder="请输入图书简介" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bookDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBook" :loading="bookSubmitting">
          {{ isEditBook ? '保存修改' : '添加图书' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- ==================== 添加/编辑用户弹窗 ==================== -->
    <el-dialog v-model="userDialogVisible" :title="userDialogTitle" width="500px" @close="resetUserForm">
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username" v-if="!isEditUser">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEditUser">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" style="width:100%">
            <el-option label="普通会员" value="MEMBER" />
            <el-option label="VIP会员" value="VIP" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUser" :loading="userSubmitting">
          {{ isEditUser ? '保存修改' : '添加用户' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { getStats, getUserList, updateUserStatus, getBookList, updateBookStatus, getBorrowList, getOrderList } from '@/api/admin'
import { addBook, updateBook } from '@/api/book'
import { register as registerApi } from '@/api/user'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()

const activeTab = ref('users')
const stats = ref({ userCount: 0, bookCount: 0, borrowCount: 0, orderCount: 0 })

// 用户
const userLoading = ref(false), users = ref([]), userPage = ref(1), userTotal = ref(0)
// 图书
const bookLoading = ref(false), books = ref([]), bookPage = ref(1), bookTotal = ref(0)
// 借阅
const borrowLoading = ref(false), borrows = ref([]), borrowPage = ref(1), borrowTotal = ref(0)
// 订单
const orderLoading = ref(false), orders = ref([]), orderPage = ref(1), orderTotal = ref(0)

// 图书弹窗
const bookDialogVisible = ref(false)
const isEditBook = ref(false)
const bookSubmitting = ref(false)
const bookFormRef = ref(null)
const bookForm = reactive({
  id: null, title: '', author: '', isbn: '', publisher: '', categoryId: 9, salePrice: 0, totalStock: 0, borrowMode: 'SINGLE', description: ''
})
const bookRules = {
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'blur' }],
  salePrice: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  totalStock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}
const bookDialogTitle = ref('')

// 用户弹窗
const userDialogVisible = ref(false)
const isEditUser = ref(false)
const userSubmitting = ref(false)
const userFormRef = ref(null)
const userForm = reactive({
  id: null, username: '', password: '', nickname: '', email: '', phone: '', role: 'MEMBER'
})
const userRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '至少6位', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'blur' }]
}
const userDialogTitle = ref('')

onMounted(() => { fetchStats(); fetchUsers() })
watch(activeTab, (t) => { if (t === 'books') fetchBooks(); if (t === 'borrows') fetchBorrows(); if (t === 'orders') fetchOrders() })

async function fetchStats() {
  try { const r = await getStats(); stats.value = r.data } catch (e) { /* */ }
}
async function fetchUsers() {
  userLoading.value = true
  try { const r = await getUserList(userPage.value); users.value = r.data?.records || []; userTotal.value = r.data?.total || 0 } catch (e) { users.value = [] }
  finally { userLoading.value = false }
}
async function fetchBooks() {
  bookLoading.value = true
  try { const r = await getBookList(bookPage.value); books.value = r.data?.records || []; bookTotal.value = r.data?.total || 0 } catch (e) { books.value = [] }
  finally { bookLoading.value = false }
}
async function fetchBorrows() {
  borrowLoading.value = true
  try { const r = await getBorrowList(borrowPage.value); borrows.value = r.data?.records || []; borrowTotal.value = r.data?.total || 0 } catch (e) { borrows.value = [] }
  finally { borrowLoading.value = false }
}
async function fetchOrders() {
  orderLoading.value = true
  try { const r = await getOrderList(orderPage.value); orders.value = r.data?.records || []; orderTotal.value = r.data?.total || 0 } catch (e) { orders.value = [] }
  finally { orderLoading.value = false }
}

// 用户操作
async function toggleUserStatus(row) {
  const s = row.status === 0 ? 1 : 0
  try { await ElMessageBox.confirm(`确定${s === 1 ? '禁用' : '启用'}该用户？`, '确认', { type: 'warning' }); await updateUserStatus(row.id, s); ElMessage.success('操作成功'); fetchUsers() } catch (e) { /* */ }
}
function openAddUser() {
  isEditUser.value = false
  userDialogTitle.value = '添加用户'
  resetUserForm()
  userDialogVisible.value = true
}
function openEditUser(row) {
  isEditUser.value = true
  userDialogTitle.value = '编辑用户'
  userForm.id = row.id
  userForm.username = row.username
  userForm.password = ''
  userForm.nickname = row.nickname || ''
  userForm.email = row.email || ''
  userForm.phone = row.phone || ''
  userForm.role = row.role
  userDialogVisible.value = true
}
function resetUserForm() {
  userForm.id = null; userForm.username = ''; userForm.password = ''; userForm.nickname = ''; userForm.email = ''; userForm.phone = ''; userForm.role = 'MEMBER'
  userFormRef.value?.resetFields()
}
async function submitUser() {
  const valid = await userFormRef.value.validate().catch(() => false)
  if (!valid) return
  userSubmitting.value = true
  try {
    if (isEditUser.value) {
      await userStore.updateProfile({
        nickname: userForm.nickname, email: userForm.email, phone: userForm.phone
      })
      ElMessage.success('修改成功')
    } else {
      await registerApi({ username: userForm.username, password: userForm.password, nickname: userForm.nickname, email: userForm.email, phone: userForm.phone, role: userForm.role })
      ElMessage.success('添加成功')
    }
    userDialogVisible.value = false
    fetchUsers()
  } catch (e) { /* */ }
  finally { userSubmitting.value = false }
}

// 图书操作
async function toggleBookStatus(row) {
  const s = row.status === 1 ? 0 : 1
  try { await ElMessageBox.confirm(`确定${s === 1 ? '上架' : '下架'}该图书？`, '确认', { type: 'warning' }); await updateBookStatus(row.id, s); ElMessage.success('操作成功'); fetchBooks() } catch (e) { /* */ }
}
function openAddBook() {
  isEditBook.value = false
  bookDialogTitle.value = '添加图书'
  resetBookForm()
  bookDialogVisible.value = true
}
function openEditBook(row) {
  isEditBook.value = true
  bookDialogTitle.value = '编辑图书'
  Object.assign(bookForm, {
    id: row.id, title: row.title, author: row.author, isbn: row.isbn || '', publisher: row.publisher || '',
    categoryId: row.categoryId, salePrice: row.salePrice, totalStock: row.totalStock,
    borrowMode: row.borrowMode, description: row.description || ''
  })
  bookDialogVisible.value = true
}
function resetBookForm() {
  Object.assign(bookForm, { id: null, title: '', author: '', isbn: '', publisher: '', categoryId: 9, salePrice: 0, totalStock: 0, borrowMode: 'SINGLE', description: '' })
  bookFormRef.value?.resetFields()
}
async function submitBook() {
  const valid = await bookFormRef.value.validate().catch(() => false)
  if (!valid) return
  bookSubmitting.value = true
  try {
    const data = { ...bookForm }
    if (isEditBook.value) {
      await updateBook(bookForm.id, data)
      ElMessage.success('修改成功')
    } else {
      await addBook(data)
      ElMessage.success('添加成功')
    }
    bookDialogVisible.value = false
    fetchBooks()
    fetchStats()
  } catch (e) { /* */ }
  finally { bookSubmitting.value = false }
}

function statusTag(s) {
  const m = { PENDING: 'warning', PROCESSING: 'primary', SHIPPED: 'success', DELIVERED: 'success', CANCELLED: 'info' }
  return m[s] || 'info'
}
</script>

<style scoped>
.admin-dashboard h2 { font-size: 22px; margin-bottom: 20px; }
.stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
.stat-card { background: #fff; border-radius: 8px; padding: 24px; text-align: center; }
.stat-value { font-size: 32px; font-weight: bold; color: #409eff; }
.stat-label { font-size: 14px; color: #999; margin-top: 8px; }
.admin-tabs { background: #fff; border-radius: 8px; padding: 20px; }
.tab-toolbar { margin-bottom: 16px; display: flex; gap: 8px; }
</style>