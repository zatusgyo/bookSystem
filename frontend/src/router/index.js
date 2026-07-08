import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/pay',
    name: 'MockPay',
    component: () => import('@/views/purchase/MockPay.vue'),
    meta: { title: '支付', requiresAuth: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'books',
        name: 'BookList',
        component: () => import('@/views/book/BookList.vue'),
        meta: { title: '图书浏览' }
      },
      {
        path: 'books/:id',
        name: 'BookDetail',
        component: () => import('@/views/book/BookDetail.vue'),
        meta: { title: '图书详情' }
      },
      {
        path: 'borrow',
        name: 'BorrowList',
        component: () => import('@/views/borrow/BorrowList.vue'),
        meta: { title: '我的借阅', requiresAuth: true }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/purchase/Cart.vue'),
        meta: { title: '购物车', requiresAuth: true }
      },
      {
        path: 'orders',
        name: 'OrderList',
        component: () => import('@/views/order/OrderList.vue'),
        meta: { title: '我的订单', requiresAuth: true }
      },
      {
        path: 'user',
        name: 'UserCenter',
        component: () => import('@/views/user/UserCenter.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'admin',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/AdminDashboard.vue'),
        meta: { title: '管理员后台', requiresAuth: true, requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 图书借阅与销售平台` : '图书借阅与销售平台'

  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    return next('/login')
  }

  if (to.meta.requiresAdmin) {
    // 简单检查：从 localStorage 读取角色（实际应由后端校验）
    const role = localStorage.getItem('userRole')
    if (role !== 'ADMIN') {
      return next('/')
    }
  }

  next()
})

export default router