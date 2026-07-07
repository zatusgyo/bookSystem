<template>
  <div class="user-center">
    <h2>个人中心</h2>

    <div class="user-card" v-loading="loading">
      <el-empty v-if="!loading && !userStore.user" description="请先登录" />

      <template v-if="userStore.user">
        <div class="user-avatar">
          <el-avatar :size="80" :src="userStore.user.avatar" />
          <div class="user-name">
            <h3>{{ userStore.user.nickname || userStore.user.username }}</h3>
            <el-tag size="small" :type="userStore.user.role === 'ADMIN' ? 'danger' : 'primary'">
              {{ userStore.user.role === 'ADMIN' ? '管理员' : userStore.user.role === 'VIP' ? 'VIP会员' : '普通会员' }}
            </el-tag>
          </div>
        </div>

        <el-divider />

        <el-form :model="editForm" label-width="100px" class="user-form">
          <el-form-item label="用户名">
            <el-input :model-value="userStore.user.username" disabled />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="editForm.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="editForm.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="editForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="借阅状态">
            <span>当前借阅 {{ userStore.user.currentBorrowCount || 0 }} 本 / 最大 {{ userStore.user.maxBorrowCount || 5 }} 本</span>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSave" :loading="saving">保存修改</el-button>
          </el-form-item>
        </el-form>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const loading = ref(false)
const saving = ref(false)

const editForm = reactive({
  nickname: '',
  email: '',
  phone: ''
})

onMounted(() => {
  loadUserInfo()
})

async function loadUserInfo() {
  loading.value = true
  try {
    await userStore.fetchUserInfo()
    if (userStore.user) {
      editForm.nickname = userStore.user.nickname || ''
      editForm.email = userStore.user.email || ''
      editForm.phone = userStore.user.phone || ''
    }
  } catch (e) {
    // 忽略
  } finally {
    loading.value = false
  }
}

async function handleSave() {
  saving.value = true
  try {
    await userStore.updateProfile({
      nickname: editForm.nickname,
      email: editForm.email,
      phone: editForm.phone
    })
    ElMessage.success('保存成功')
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.user-center h2 {
  font-size: 22px;
  margin-bottom: 20px;
}

.user-card {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-name h3 {
  font-size: 20px;
  margin-bottom: 8px;
}

.user-form {
  max-width: 500px;
  margin-top: 10px;
}
</style>