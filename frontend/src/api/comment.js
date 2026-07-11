import request from '@/utils/request'

// 添加评论
export function addComment(bookId, userId, username, rating, content) {
  return request.post('/comment', { bookId, userId, username, rating, content })
}

// 获取图书评论列表
export function getBookComments(bookId) {
  return request.get(`/comment/book/${bookId}`)
}

// 获取图书平均评分
export function getAverageRating(bookId) {
  return request.get(`/comment/rating/${bookId}`)
}