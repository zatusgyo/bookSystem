import request from '@/utils/request'

// 创建订单
export function createOrder(data) {
  return request.post('/order', data)
}

// 支付订单
export function payOrder(orderId, paymentMethod = 'ALIPAY') {
  return request.put(`/order/pay/${orderId}`, null, { params: { paymentMethod } })
}

// 取消订单
export function cancelOrder(orderId) {
  return request.put(`/order/cancel/${orderId}`)
}

// 查询用户订单
export function getUserOrders(userId, page = 1, size = 10) {
  return request.get(`/order/user/${userId}`, { params: { page, size } })
}

// 查询订单详情
export function getOrderDetail(orderId) {
  return request.get(`/order/${orderId}`)
}

// 查询支付状态
export function queryPayment(tradeNo) {
  return request.get('/payment/query', { params: { tradeNo } })
}