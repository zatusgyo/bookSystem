<template>
  <div class="borrow-list">
    <h2>我的借阅</h2>

    <div v-loading="loading">
      <el-empty v-if="!loading && records.length === 0" description="暂无借阅记录" />

      <div v-for="record in records" :key="record.id" class="borrow-card">
        <div class="borrow-header">
          <span class="borrow-no">借阅编号: {{ record.borrowNo }}</span>
          <el-tag :type="record.status === 'BORROWING' ? 'primary' : record.status === 'OVERDUE' ? 'danger' : 'success'">
            {{ record.status === 'BORROWING' ? '借阅中' : record.status === 'OVERDUE' ? '已逾期' : '已归还' }}
          </el-tag>
          <span v-if="record.borrowMode === 'MULTI'" class="multi-tag">多人共读</span>
        </div>
        <div class="borrow-body">
          <div class="borrow-info">
            <span>借阅日期: {{ record.borrowDate }}</span>
            <span>应还日期: {{ record.dueDate }}</span>
            <span v-if="record.returnDate">归还日期: {{ record.returnDate }}</span>
            <span>续借次数: {{ record.renewCount }}/2</span>
            <span v-if="record.borrowFee > 0">借阅费用: ¥{{ record.borrowFee }}</span>
            <span v-if="record.overdueFine > 0" class="overdue">逾期罚款: ¥{{ record.overdueFine }}</span>
          </div>
        </div>
        <div class="borrow-actions" v-if="record.status === 'BORROWING' || record.status === 'OVERDUE'">
          <el-button type="primary" size="small" @click="handleReturn(record)" :loading="actionId === record.id">
            归还
          </el-button>
          <el-button
            type="success"
            size="small"
            plain
            @click="handleRenew(record)"
            :loading="actionId === record.id"
            :disabled="record.renewCount >= 2"
          >
            续借 (+15天)
          </el-button>
        </div>
      </div>

      <div class="pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          :page-size="size"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchRecords"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserBorrowRecords, returnBook, renewBook } from '@/api/borrow'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()

const loading = ref(false)
const actionId = ref(null)
const records = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

onMounted(() => {
  fetchRecords()
})

async function fetchRecords() {
  loading.value = true
  try {
    const res = await getUserBorrowRecords(userStore.user.id, page.value, size.value)
    if (res.data) {
      records.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    records.value = []
  } finally {
    loading.value = false
  }
}

async function handleReturn(record) {
  try {
    await ElMessageBox.confirm('确认归还该图书？', '确认归还', { type: 'info' })
  } catch (e) {
    return
  }

  actionId.value = record.id
  try {
    const res = await returnBook(record.id)
    const msg = res.data?.status === 'OVERDUE' ? '归还成功，已产生逾期罚款' : '归还成功'
    ElMessage.success(msg)
    fetchRecords()
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    actionId.value = null
  }
}

async function handleRenew(record) {
  try {
    await ElMessageBox.confirm('确认续借该图书？续借将延长15天。', '确认续借', { type: 'info' })
  } catch (e) {
    return
  }

  actionId.value = record.id
  try {
    await renewBook(record.id)
    ElMessage.success('续借成功')
    fetchRecords()
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    actionId.value = null
  }
}
</script>

<style scoped>
.borrow-list h2 {
  font-size: 22px;
  margin-bottom: 20px;
}

.borrow-card {
  background: #fff;
  border-radius: 8px;
  margin-bottom: 16px;
  padding: 16px 20px;
}

.borrow-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.borrow-no {
  font-size: 14px;
  color: #333;
  font-weight: 600;
  flex: 1;
}

.multi-tag {
  font-size: 12px;
  color: #409eff;
  border: 1px solid #409eff;
  padding: 2px 8px;
  border-radius: 4px;
}

.borrow-body {
  padding: 12px 0;
}

.borrow-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 13px;
  color: #666;
}

.overdue {
  color: #e33;
  font-weight: bold;
}

.borrow-actions {
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
</style>