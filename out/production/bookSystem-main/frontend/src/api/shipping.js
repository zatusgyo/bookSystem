import request from '@/utils/request'

// 获取订单物流轨迹
export function getOrderTracks(orderId) {
  return request.get(`/shipping/order/${orderId}`)
}