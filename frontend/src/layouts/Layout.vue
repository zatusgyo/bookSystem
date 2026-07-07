<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="header-left">
        <router-link to="/" class="logo">
          <el-icon :size="28"><Reading /></el-icon>
          <span class="logo-text">图书借阅与销售平台</span>
        </router-link>
      </div>
      <div class="header-right">
        <template v-if="userStore.isLoggedIn">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.user?.avatar" />
              <span class="username">{{ userStore.user?.nickname || userStore.user?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="user">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="$router.push('/login')">登录</el-button>
        </template>
      </div>
    </el-header>

    <el-container class="main-container">
      <el-aside class="aside" width="200px">
        <el-menu
          :default-active="activeMenu"
          router
          :collapse="false"
          class="side-menu"
        >
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/books">
            <el-icon><Search /></el-icon>
            <span>图书浏览</span>
          </el-menu-item>
          <el-menu-item index="/borrow" v-if="userStore.isLoggedIn">
            <el-icon><Notebook /></el-icon>
            <span>我的借阅</span>
          </el-menu-item>
          <el-menu-item index="/cart" v-if="userStore.isLoggedIn">
            <el-icon><ShoppingCart /></el-icon>
            <span>购物车</span>
          </el-menu-item>
          <el-menu-item index="/orders" v-if="userStore.isLoggedIn">
            <el-icon><Tickets /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import {
  Reading, ArrowDown, HomeFilled, Search, Notebook,
  ShoppingCart, Tickets
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path.startsWith('/books') ? '/books' : route.path)

function handleCommand(command) {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/')
  } else if (command === 'user') {
    router.push('/user')
  }
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  background: #f5f7fa;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  padding: 0 24px;
  height: 60px;
}

.header-left .logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  color: #303133;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
}

.username {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.main-container {
  height: calc(100vh - 60px);
}

.aside {
  background: #fff;
  border-right: 1px solid #e4e7ed;
  overflow-y: auto;
}

.side-menu {
  border-right: none;
}

.main-content {
  padding: 24px;
  overflow-y: auto;
  background: #f5f7fa;
}
</style>