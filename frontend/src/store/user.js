import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, getUserInfo, updateUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || '')
  const initialized = ref(false)

  const isLoggedIn = computed(() => !!user.value && !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  // 初始化：从 localStorage 恢复登录状态
  async function init() {
    if (initialized.value) return
    initialized.value = true
    const savedToken = localStorage.getItem('token')
    const savedUserId = localStorage.getItem('userId')
    if (savedToken && savedUserId) {
      token.value = savedToken
      try {
        const res = await getUserInfo(savedUserId)
        user.value = res.data
      } catch (e) {
        // token 过期或无效，清除
        logout()
      }
    }
  }

  // 登录
  async function login(username, password) {
    const res = await loginApi(username, password)
    user.value = res.data.user
    token.value = res.data.token
    localStorage.setItem('token', token.value)
    localStorage.setItem('userId', res.data.user.id)
    localStorage.setItem('userRole', res.data.user.role || '')
    return res.data.user
  }

  // 注册
  async function register(data) {
    const res = await registerApi(data)
    return res.data
  }

  // 获取用户信息
  async function fetchUserInfo() {
    const userId = localStorage.getItem('userId')
    if (!userId) return
    const res = await getUserInfo(userId)
    user.value = res.data
  }

  // 更新用户信息
  async function updateProfile(data) {
    const userId = user.value?.id
    if (!userId) return
    await updateUserInfo(userId, data)
    await fetchUserInfo()
  }

  // 退出登录
  function logout() {
    user.value = null
    token.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('userRole')
  }

  return { user, token, isLoggedIn, isAdmin, init, login, register, fetchUserInfo, updateProfile, logout }
})