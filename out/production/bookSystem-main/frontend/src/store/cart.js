import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  const items = ref(JSON.parse(localStorage.getItem('cartItems') || '[]'))

  // 保存到 localStorage
  function saveToStorage() {
    localStorage.setItem('cartItems', JSON.stringify(items.value))
  }

  const totalCount = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))
  const totalAmount = computed(() => items.value.reduce((sum, item) => sum + item.salePrice * item.quantity, 0))

  // 添加到购物车
  function addToCart(book) {
    const exist = items.value.find(item => item.id === book.id)
    if (exist) {
      exist.quantity++
    } else {
      items.value.push({ ...book, quantity: 1 })
    }
    saveToStorage()
  }

  // 修改数量
  function updateQuantity(bookId, quantity) {
    const item = items.value.find(item => item.id === bookId)
    if (item) {
      item.quantity = Math.max(1, quantity)
      saveToStorage()
    }
  }

  // 移除商品
  function removeItem(bookId) {
    items.value = items.value.filter(item => item.id !== bookId)
    saveToStorage()
  }

  // 清空购物车
  function clearCart() {
    items.value = []
    saveToStorage()
  }

  return { items, totalCount, totalAmount, addToCart, updateQuantity, removeItem, clearCart }
})