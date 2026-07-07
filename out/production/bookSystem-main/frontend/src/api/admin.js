import request from '@/utils/request'

// 获取统计数据
export function getStats() {
  return request.get('/admin/stats')
}

// 用户列表
export function getUserList(page = 1, size = 10, keyword) {
  return request.get('/admin/users', { params: { page, size, keyword } })
}

// 启用/禁用用户
export function updateUserStatus(id, status) {
  return request.put(`/admin/users/${id}/status`, null, { params: { status } })
}

// 图书列表（含已下架）
export function getBookList(page = 1, size = 10, keyword) {
  return request.get('/admin/books', { params: { page, size, keyword } })
}

// 上架/下架图书
export function updateBookStatus(id, status) {
  return request.put(`/admin/books/${id}/status`, null, { params: { status } })
}

// 借阅记录
export function getBorrowList(page = 1, size = 10) {
  return request.get('/admin/borrows', { params: { page, size } })
}

// 订单
export function getOrderList(page = 1, size = 10) {
  return request.get('/admin/orders', { params: { page, size } })
}