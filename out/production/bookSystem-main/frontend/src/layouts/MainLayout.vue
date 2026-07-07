<template>
  <div class="main-layout">
    <NavBar />
    <main class="main-content">
      <router-view />
    </main>
    <footer class="main-footer">
      <p>图书借阅与销售平台 - 学生项目</p>
    </footer>
  </div>
</template>

<script setup>
import NavBar from './NavBar.vue'
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.main-content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 20px auto;
  padding: 0 20px;
}

.main-footer {
  text-align: center;
  padding: 20px;
  color: #999;
  font-size: 13px;
  border-top: 1px solid #eee;
  background: #fff;
}
</style>