<template>
  <div class="cart-page">
    <h2>购物车</h2>

    <el-empty v-if="cartStore.items.length === 0" description="购物车是空的">
      <el-button type="primary" @click="$router.push('/books')">去逛逛</el-button>
    </el-empty>

    <template v-else>
      <div class="cart-list">
        <div class="cart-header">
          <span class="col-name">商品</span>
          <span class="col-price">单价</span>
          <span class="col-qty">数量</span>
          <span class="col-subtotal">小计</span>
          <span class="col-action">操作</span>
        </div>
        <div v-for="item in cartStore.items" :key="item.id" class="cart-item">
          <div class="col-name">
            <img :src="item.coverImage || defaultCover" class="item-cover" />
            <div class="item-info">
              <p class="item-title">{{ item.title }}</p>
              <p class="item-author">{{ item.author }}</p>
            </div>
          </div>
          <span class="col-price">¥{{ item.salePrice }}</span>
          <div class="col-qty">
            <el-input-number
              v-model="item.quantity"
              :min="1"
              :max="99"
              size="small"
              @change="cartStore.updateQuantity(item.id, item.quantity)"
            />
          </div>
          <span class="col-subtotal">¥{{ (item.salePrice * item.quantity).toFixed(2) }}</span>
          <span class="col-action">
            <el-button type="danger" text @click="cartStore.removeItem(item.id)">删除</el-button>
          </span>
        </div>
      </div>

      <div class="cart-footer">
        <div class="footer-left">
          <el-button @click="cartStore.clearCart()">清空购物车</el-button>
        </div>
        <div class="footer-right">
          <span class="total-text">
            共 <strong>{{ cartStore.totalCount }}</strong> 件，合计：
            <span class="total-amount">¥{{ cartStore.totalAmount.toFixed(2) }}</span>
          </span>
          <el-button type="primary" size="large" @click="handleCheckout" :loading="submitting">
            去结算
          </el-button>
        </div>
      </div>
    </template>

    <!-- 结算弹窗 -->
    <el-dialog v-model="checkoutVisible" title="填写收货信息" width="500px">
      <el-form :model="orderForm" :rules="orderRules" ref="orderFormRef" label-width="80px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="orderForm.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="orderForm.receiverPhone" placeholder="请输入收货人手机号" />
        </el-form-item>
        <el-form-item label="收货地址" prop="receiverAddress">
          <el-input v-model="orderForm.receiverAddress" type="textarea" placeholder="请输入收货地址" />
        </el-form-item>
        <el-form-item label="订单金额">
          <span class="checkout-amount">¥{{ cartStore.totalAmount.toFixed(2) }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkoutVisible = false">取消</el-button>
        <el-button type="primary" @click="submitOrder" :loading="submitting">提交订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/store/cart'
import { useUserStore } from '@/store/user'
import { createOrder } from '@/api/order'
import { ElMessage } from 'element-plus'
import { DEFAULT_COVER } from '@/utils/constants'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const submitting = ref(false)
const checkoutVisible = ref(false)
const orderFormRef = ref(null)
const defaultCover = DEFAULT_COVER

const orderForm = reactive({
  receiverName: '',
  receiverPhone: '',
  receiverAddress: ''
})

const orderRules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  receiverAddress: [{ required: true, message: '请输入收货地址', trigger: 'blur' }]
}

function handleCheckout() {
  checkoutVisible.value = true
}

async function submitOrder() {
  const valid = await orderFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const bookIds = cartStore.items.map(item => item.id)
    const quantities = cartStore.items.map(item => item.quantity)
    await createOrder({
      userId: userStore.user.id,
      bookIds,
      quantities,
      receiverName: orderForm.receiverName,
      receiverPhone: orderForm.receiverPhone,
      receiverAddress: orderForm.receiverAddress
    })
    ElMessage.success('订单创建成功')
    cartStore.clearCart()
    checkoutVisible.value = false
    router.push('/orders')
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.cart-page h2 {
  font-size: 22px;
  margin-bottom: 20px;
}

.cart-list {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.cart-header,
.cart-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  gap: 16px;
}

.cart-header {
  background: #fafafa;
  font-size: 14px;
  color: #666;
  font-weight: 600;
}

.cart-item {
  border-bottom: 1px solid #f0f0f0;
}

.col-name {
  flex: 3;
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-cover {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  background: #f0f0f0;
}

.item-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.item-author {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.col-price { flex: 1; color: #333; }
.col-qty { flex: 1; }
.col-subtotal { flex: 1; color: #e33; font-weight: bold; }
.col-action { flex: 0.8; text-align: center; }

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}

.total-text {
  font-size: 16px;
  margin-right: 16px;
}

.total-amount {
  font-size: 22px;
  color: #e33;
  font-weight: bold;
}

.checkout-amount {
  font-size: 20px;
  color: #e33;
  font-weight: bold;
}
</style>