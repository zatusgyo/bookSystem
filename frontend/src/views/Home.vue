<template>
  <div class="home">
    <!-- 轮播图 -->
    <el-carousel height="360px" :interval="4000" class="home-carousel">
      <el-carousel-item v-for="(banner, i) in banners" :key="i">
        <div class="banner-item" :style="{ background: banner.bg }">
          <div class="banner-text">
            <h2>{{ banner.title }}</h2>
            <p>{{ banner.desc }}</p>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 推荐图书 -->
    <section class="home-section">
      <div class="section-header">
        <h3>推荐图书</h3>
        <el-button text type="primary" @click="$router.push('/books')">查看更多</el-button>
      </div>
      <div class="book-grid" v-loading="loading">
        <el-empty v-if="!loading && books.length === 0" description="暂无图书" />
        <div v-for="book in books" :key="book.id" class="book-card" @click="$router.push(`/books/${book.id}`)">
          <img :src="book.coverImage || defaultCover" :alt="book.title" class="book-cover" />
          <div class="book-info">
            <p class="book-title">{{ book.title }}</p>
            <p class="book-author">{{ book.author }}</p>
            <p class="book-price">
              <span class="sale-price">¥{{ book.salePrice }}</span>
              <span v-if="book.borrowMode === 'MULTI'" class="multi-tag">共读</span>
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- 热门借阅 -->
    <section class="home-section" v-if="hotBooks.length > 0">
      <div class="section-header">
        <h3>热门借阅</h3>
      </div>
      <div class="book-grid">
        <div v-for="book in hotBooks" :key="book.id" class="book-card" @click="$router.push(`/books/${book.id}`)">
          <img :src="book.coverImage || defaultCover" :alt="book.title" class="book-cover" />
          <div class="book-info">
            <p class="book-title">{{ book.title }}</p>
            <p class="book-author">{{ book.author }}</p>
            <p class="book-price">
              <span class="borrow-count">已借阅 {{ book.borrowCount }} 次</span>
            </p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { searchBooks } from '@/api/book'

const loading = ref(false)
const books = ref([])
const hotBooks = ref([])
const defaultCover = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=A+simple+book+cover+design+with+clean+geometric+patterns+and+soft+colors,+minimalist+style,+suitable+for+a+generic+book+cover&image_size=square'

const banners = [
  { title: '海量图书·畅享阅读', desc: '涵盖计算机、文学、历史等多个领域', bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { title: '多人共读·分享快乐', desc: '支持多人共读模式，和朋友一起阅读', bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { title: '在线购买·便捷支付', desc: '支持支付宝、微信支付，快速下单', bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' }
]

onMounted(() => {
  fetchBooks()
})

async function fetchBooks() {
  loading.value = true
  try {
    const res = await searchBooks({ page: 1, size: 8 })
    books.value = res.data?.records || []
    // 按借阅次数排序作为热门
    hotBooks.value = [...books.value].sort((a, b) => (b.borrowCount || 0) - (a.borrowCount || 0)).slice(0, 4)
  } catch (e) {
    // 忽略
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.home-carousel {
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 40px;
}

.banner-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-text {
  text-align: center;
  color: #fff;
}

.banner-text h2 {
  font-size: 36px;
  margin-bottom: 12px;
}

.banner-text p {
  font-size: 18px;
  opacity: 0.9;
}

.home-section {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 20px;
  color: #333;
  border-left: 4px solid #409eff;
  padding-left: 12px;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.book-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.book-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.book-cover {
  width: 100%;
  height: 200px;
  object-fit: cover;
  background: #f0f0f0;
}

.book-info {
  padding: 12px;
}

.book-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author {
  font-size: 13px;
  color: #999;
  margin: 4px 0;
}

.book-price {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 6px;
}

.sale-price {
  font-size: 16px;
  color: #e33;
  font-weight: bold;
}

.multi-tag {
  font-size: 11px;
  color: #409eff;
  border: 1px solid #409eff;
  padding: 1px 6px;
  border-radius: 4px;
}

.borrow-count {
  font-size: 12px;
  color: #999;
}
</style>