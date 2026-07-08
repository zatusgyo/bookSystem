import request from '@/utils/request'

// 搜索图书
export function searchBooks(params) {
  return request.get('/book/search', { params })
}

// 获取图书详情
export function getBookDetail(id) {
  return request.get(`/book/${id}`)
}

// 添加图书（管理员）
export function addBook(data) {
  return request.post('/book', data)
}

// 更新图书（管理员）
export function updateBook(id, data) {
  return request.put(`/book/${id}`, data)
}