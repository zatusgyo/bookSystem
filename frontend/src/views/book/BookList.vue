<template>
  <div class="book-list">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="搜索书名、作者、ISBN"
        clearable
        @keyup.enter="handleSearch"
        @clear="handleSearch"
        class="search-input"
      >
        <template #append>
          <el-button @click="handleSearch" :icon="Search">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- 分类筛选 -->
    <div class="category-filter">
      <el-button :type="selectedCategory === null ? 'primary' : 'default'" size="small" @click="filterByCategory(null)">
        全部
      </el-button>
      <el-button
        v-for="cat in categories"
        :key="cat.id"
        :type="selectedCategory === cat.id ? 'primary' : 'default'"
        size="small"
        @click="filterByCategory(cat.id)"
      >
        {{ cat.name }}
      </el-button>
    </div>

    <!-- 图书列表 -->
    <div v-loading="loading" class="book-grid">
      <el-empty v-if="!loading && books.length === 0" description="未找到相关图书" />
      <div v-for="book in books" :key="book.id" class="book-card" @click="$router.push(`/books/${book.id}`)">
        <img :src="book.coverImage || defaultCover" :alt="book.title" class="book-cover" />
        <div class="book-info">
          <p class="book-title">{{ book.title }}</p>
          <p class="book-author">{{ book.author }} / {{ book.publisher }}</p>
          <p class="book-desc">{{ book.description }}</p>
          <div class="book-bottom">
            <span class="sale-price">¥{{ book.salePrice }}</span>
            <span class="stock">库存: {{ book.availableStock }}</span>
            <span v-if="book.borrowMode === 'MULTI'" class="multi-tag">支持共读</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="page"
        :page-size="size"
        :total="total"
        layout="prev, pager, next, total"
        @current-change="handleSearch"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { searchBooks } from '@/api/book'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { DEFAULT_COVER } from '@/utils/constants'

const loading = ref(false)
const keyword = ref('')
const selectedCategory = ref(null)
const page = ref(1)
const size = ref(12)
const total = ref(0)
const books = ref([])
const categories = ref([])
const defaultCover = DEFAULT_COVER

onMounted(() => {
  fetchCategories()
  handleSearch()
})

async function fetchCategories() {
  try {
    const res = await request.get('/book/search?page=1&size=100')
    // 分类从后端获取，这里简化处理：直接使用常见分类
    categories.value = [
      { id: 1, name: '计算机科学' },
      { id: 2, name: '文学小说' },
      { id: 3, name: '历史哲学' },
      { id: 4, name: '经济管理' },
      { id: 5, name: '科学技术' },
      { id: 6, name: '教育考试' },
      { id: 7, name: '艺术设计' },
      { id: 8, name: '生活百科' }
    ]
  } catch (e) {
    // 使用默认分类
    categories.value = [
      { id: 1, name: '计算机科学' },
      { id: 2, name: '文学小说' },
      { id: 3, name: '历史哲学' },
      { id: 4, name: '经济管理' }
    ]
  }
}

function filterByCategory(catId) {
  selectedCategory.value = catId
  page.value = 1
  handleSearch()
}

async function handleSearch() {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (keyword.value) params.keyword = keyword.value
    if (selectedCategory.value) params.categoryId = selectedCategory.value

    const res = await searchBooks(params)
    if (res.data) {
      books.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    books.value = []
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.search-bar {
  margin-bottom: 20px;
}

.search-input {
  max-width: 500px;
}

.category-filter {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  min-height: 300px;
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
  font-size: 12px;
  color: #999;
  margin: 4px 0;
}

.book-desc {
  font-size: 12px;
  color: #666;
  margin: 6px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.book-bottom {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.sale-price {
  font-size: 16px;
  color: #e33;
  font-weight: bold;
}

.stock {
  font-size: 12px;
  color: #999;
}

.multi-tag {
  font-size: 11px;
  color: #409eff;
  border: 1px solid #409eff;
  padding: 1px 6px;
  border-radius: 4px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>