import { createRouter, createWebHistory } from 'vue-router'

/**
 * 路由配置
 * TODO: 组员根据实际页面补充路由
 */
const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/books',
    name: 'BookList',
    component: () => import('@/views/book/BookList.vue'),
    meta: { title: '图书浏览' }
  },
  {
    path: '/books/:id',
    name: 'BookDetail',
    component: () => import('@/views/book/BookDetail.vue'),
    meta: { title: '图书详情' }
  },
  {
    path: '/borrow',
    name: 'BorrowList',
    component: () => import('@/views/borrow/BorrowList.vue'),
    meta: { title: '我的借阅' }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/purchase/Cart.vue'),
    meta: { title: '购物车' }
  },
  {
    path: '/orders',
    name: 'OrderList',
    component: () => import('@/views/order/OrderList.vue'),
    meta: { title: '我的订单' }
  },
  {
    path: '/user',
    name: 'UserCenter',
    component: () => import('@/views/user/UserCenter.vue'),
    meta: { title: '个人中心' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 页面标题
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 图书借阅与销售平台` : '图书借阅与销售平台'
  next()
})

export default router
