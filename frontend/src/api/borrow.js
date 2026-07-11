import request from '@/utils/request'

// 借阅图书
export function borrowBook(userId, bookId, borrowMode) {
  return request.post('/borrow', { userId, bookId, borrowMode })
}

// 归还图书
export function returnBook(recordId) {
  return request.put(`/borrow/return/${recordId}`)
}

// 续借图书
export function renewBook(recordId) {
  return request.put(`/borrow/renew/${recordId}`)
}

// 查询用户借阅记录
export function getUserBorrowRecords(userId, page = 1, size = 10) {
  return request.get(`/borrow/user/${userId}`, { params: { page, size } })
}