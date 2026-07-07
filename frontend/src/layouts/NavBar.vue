<template>
  <el-menu
    :default-active="activeIndex"
    mode="horizontal"
    :ellipsis="false"
    router
    class="navbar"
  >
    <div class="navbar-brand">
      <el-menu-item index="/">
        <span class="brand-title">图书借阅与销售平台</span>
      </el-menu-item>
    </div>

    <div class="navbar-menu">
      <el-menu-item index="/books">图书浏览</el-menu-item>
      <el-menu-item index="/cart" v-if="userStore.isLoggedIn">
        购物车
        <el-badge :value="cartStore.totalCount" :hidden="cartStore.totalCount === 0" />
      </el-menu-item>
      <el-menu-item index="/borrow" v-if="userStore.isLoggedIn">我的借阅</el-menu-item>
      <el-menu-item index="/orders" v-if="userStore.isLoggedIn">我的订单</el-menu-item>
      <el-menu-item index="/admin" v-if="userStore.isAdmin">管理后台</el-menu-item>
    </div>

    <div class="navbar-user">
      <template v-if="userStore.isLoggedIn">
        <el-menu-item index="/user">
          <el-icon><UserFilled /></el-icon>
          {{ userStore.user?.nickname || userStore.user?.username }}
        </el-menu-item>
        <el-menu-item index="" @click="handleLogout">退出</el-menu-item>
      </template>
      <template v-else>
        <el-menu-item index="/login">登录</el-menu-item>
      </template>
    </div>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useCartStore } from '@/store/cart'
import { UserFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const activeIndex = computed(() => route.path)

function handleLogout() {
  userStore.logout()
  cartStore.clearCart()
  ElMessage.success('已退出登录')
  router.push('/')
}
</script>

<style scoped>
.navbar {
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 60px;
}

.navbar-brand {
  flex-shrink: 0;
}

.brand-title {
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
}

.navbar-menu {
  display: flex;
  flex: 1;
  justify-content: center;
}

.navbar-user {
  display: flex;
  flex-shrink: 0;
}
</style>