<template>
  <div class="book-detail" v-loading="loading">
    <el-empty v-if="!loading && !book" description="图书不存在" />

    <template v-if="book">
      <div class="detail-main">
        <div class="detail-cover">
          <img :src="book.coverImage || defaultCover" :alt="book.title" />
        </div>
        <div class="detail-info">
          <h1>{{ book.title }}</h1>
          <div class="info-meta">
            <span>作者: {{ book.author || '未知' }}</span>
            <span>出版社: {{ book.publisher || '未知' }}</span>
            <span>ISBN: {{ book.isbn || '暂无' }}</span>
            <span>出版日期: {{ book.publishDate || '暂无' }}</span>
          </div>
          <div class="info-stats">
            <span>库存: {{ book.availableStock }} / {{ book.totalStock }}</span>
            <span>已借阅: {{ book.borrowCount }} 次</span>
            <span v-if="book.borrowMode === 'MULTI'" class="multi-tag">支持多人共读 (最多{{ book.maxCoReadCount }}人)</span>
          </div>
          <div class="info-price">
            <span class="sale-price">¥{{ book.salePrice }}</span>
            <span class="borrow-price">借阅: ¥{{ book.borrowPricePerDay || 0 }}/天</span>
          </div>
          <div class="info-actions">
            <el-button type="primary" size="large" @click="handleBorrow" :loading="borrowing">
              立即借阅
            </el-button>
            <el-button type="success" size="large" @click="handleAddToCart" :disabled="book.availableStock <= 0">
              加入购物车
            </el-button>
          </div>
        </div>
      </div>

      <!-- 借阅弹窗 -->
      <el-dialog v-model="borrowDialogVisible" title="确认借阅" width="450px">
        <el-form label-width="80px">
          <el-form-item label="图书">
            <span>{{ book.title }}</span>
          </el-form-item>
          <el-form-item label="借阅模式">
            <el-radio-group v-model="borrowMode">
              <el-radio label="SINGLE">单人借阅</el-radio>
              <el-radio label="MULTI" v-if="book.borrowMode === 'MULTI'">多人共读</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="借阅天数">
            <span>30天（默认）</span>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="borrowDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmBorrow" :loading="borrowing">确认借阅</el-button>
        </template>
      </el-dialog>

      <!-- 图书简介 -->
      <div class="detail-desc" v-if="book.description">
        <h3>图书简介</h3>
        <p>{{ book.description }}</p>
      </div>

      <!-- 图书评论 -->
      <div class="comment-section">
        <div class="comment-header">
          <h3>读者评论</h3>
          <span v-if="avgRating != null" class="avg-rating">
            <el-rate :model-value="avgRating" disabled show-score text-color="#ff9900" />
            <span class="comment-count">({{ comments.length }}条评论)</span>
          </span>
          <span v-else class="no-rating">暂无评分</span>
        </div>

        <!-- 发表评论 -->
        <div class="comment-form" v-if="userStore.isLoggedIn">
          <div class="comment-rating">
            <span class="label">评分：</span>
            <el-rate v-model="commentForm.rating" />
          </div>
          <el-input
            v-model="commentForm.content"
            type="textarea"
            :rows="3"
            placeholder="写下你的评论..."
            maxlength="500"
            show-word-limit
          />
          <el-button type="primary" @click="submitComment" :loading="commentSubmitting" style="margin-top:10px">
            发表评论
          </el-button>
        </div>
        <div class="comment-login-tip" v-else>
          <el-button text type="primary" @click="$router.push('/login')">登录后发表评论</el-button>
        </div>

        <!-- 评论列表 -->
        <div class="comment-list" v-if="comments.length > 0">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-user">
              <span class="comment-username">{{ comment.username }}</span>
              <el-rate :model-value="comment.rating" disabled size="small" />
              <span class="comment-time">{{ comment.createTime }}</span>
            </div>
            <p class="comment-content">{{ comment.content }}</p>
          </div>
        </div>
        <el-empty v-else-if="!loading" description="暂无评论，快来抢沙发" :image-size="80" />
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getBookDetail } from '@/api/book'
import { borrowBook } from '@/api/borrow'
import { addComment, getBookComments, getAverageRating } from '@/api/comment'
import { useUserStore } from '@/store/user'
import { useCartStore } from '@/store/cart'
import { ElMessage } from 'element-plus'
import { DEFAULT_COVER } from '@/utils/constants'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const loading = ref(false)
const borrowing = ref(false)
const book = ref(null)
const borrowDialogVisible = ref(false)
const borrowMode = ref('SINGLE')
const defaultCover = DEFAULT_COVER

// 评论相关
const comments = ref([])
const avgRating = ref(null)
const commentSubmitting = ref(false)
const commentForm = reactive({ rating: 5, content: '' })

onMounted(() => {
  fetchBookDetail()
  fetchComments()
})

async function fetchBookDetail() {
  loading.value = true
  try {
    const res = await getBookDetail(route.params.id)
    book.value = res.data
  } catch (e) {
    book.value = null
  } finally {
    loading.value = false
  }
}

function handleBorrow() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  borrowMode.value = 'SINGLE'
  borrowDialogVisible.value = true
}

async function confirmBorrow() {
  borrowing.value = true
  try {
    await borrowBook(userStore.user.id, book.value.id, borrowMode.value)
    ElMessage.success('借阅成功')
    borrowDialogVisible.value = false
    fetchBookDetail() // 刷新库存
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    borrowing.value = false
  }
}

function handleAddToCart() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  cartStore.addToCart({
    id: book.value.id,
    title: book.value.title,
    author: book.value.author,
    coverImage: book.value.coverImage,
    salePrice: book.value.salePrice
  })
  ElMessage.success('已加入购物车')
}

// 获取评论列表和评分
async function fetchComments() {
  try {
    const [commentRes, ratingRes] = await Promise.all([
      getBookComments(route.params.id),
      getAverageRating(route.params.id)
    ])
    comments.value = commentRes.data || []
    avgRating.value = ratingRes.data
  } catch (e) {
    comments.value = []
  }
}

// 提交评论
async function submitComment() {
  if (!commentForm.content.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  commentSubmitting.value = true
  try {
    await addComment(
      book.value.id,
      userStore.user.id,
      userStore.user.nickname || userStore.user.username,
      commentForm.rating,
      commentForm.content
    )
    ElMessage.success('评论发表成功')
    commentForm.content = ''
    commentForm.rating = 5
    fetchComments()
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    commentSubmitting.value = false
  }
}
</script>

<style scoped>
.detail-main {
  display: flex;
  gap: 30px;
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 20px;
}

.detail-cover {
  flex-shrink: 0;
  width: 260px;
  height: 360px;
}

.detail-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
  background: #f0f0f0;
}

.detail-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.detail-info h1 {
  font-size: 24px;
  color: #333;
  margin-bottom: 16px;
}

.info-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 12px;
}

.info-meta span {
  font-size: 14px;
  color: #666;
}

.info-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.info-stats span {
  font-size: 13px;
  color: #999;
}

.multi-tag {
  color: #409eff !important;
  border: 1px solid #409eff;
  padding: 1px 8px;
  border-radius: 4px;
}

.info-price {
  display: flex;
  align-items: baseline;
  gap: 16px;
  margin-bottom: 24px;
  padding: 16px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.sale-price {
  font-size: 28px;
  color: #e33;
  font-weight: bold;
}

.borrow-price {
  font-size: 14px;
  color: #999;
}

.info-actions {
  display: flex;
  gap: 12px;
  margin-top: auto;
}

.detail-desc {
  background: #fff;
  border-radius: 12px;
  padding: 24px 30px;
}

.detail-desc h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 12px;
  border-left: 4px solid #409eff;
  padding-left: 12px;
}

.detail-desc p {
  font-size: 15px;
  color: #555;
  line-height: 1.8;
}

/* 评论区域 */
.comment-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px 30px;
  margin-top: 20px;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.comment-header h3 {
  font-size: 18px;
  color: #333;
  border-left: 4px solid #409eff;
  padding-left: 12px;
}

.avg-rating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.comment-count {
  font-size: 13px;
  color: #999;
}

.no-rating {
  font-size: 13px;
  color: #ccc;
}

.comment-form {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-rating {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.comment-rating .label {
  font-size: 14px;
  color: #666;
  margin-right: 8px;
}

.comment-login-tip {
  text-align: center;
  padding: 20px;
  margin-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.comment-username {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #ccc;
  margin-left: auto;
}

.comment-content {
  font-size: 14px;
  color: #555;
  line-height: 1.6;
}
</style>