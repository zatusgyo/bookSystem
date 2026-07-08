import request from '@/utils/request'

// 发起模拟支付（获取交易流水号）
export function initiatePayment(orderId, paymentMethod = 'ALIPAY') {
  return request.post(`/order/pay/${orderId}/initiate`, null, { params: { paymentMethod } })
}

// 确认支付（模拟回调）
export function confirmPayment(tradeNo) {
  return request.put('/payment/confirm', { tradeNo })
}

// 查询支付状态
export function queryPayment(tradeNo) {
  return request.get('/payment/query', { params: { tradeNo } })
}