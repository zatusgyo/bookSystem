import request from '@/utils/request'

// 用户注册
export function register(data) {
  return request.post('/user/register', data)
}

// 用户登录
export function login(username, password) {
  return request.post('/user/login', { username, password })
}

// 获取用户信息
export function getUserInfo(id) {
  return request.get(`/user/${id}`)
}

// 更新用户信息
export function updateUserInfo(id, data) {
  return request.put(`/user/${id}`, data)
}