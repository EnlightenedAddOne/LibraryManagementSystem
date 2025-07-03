<template>
  <div class="console">
    <BookStatistics></BookStatistics>

    <!-- 图书馆公告和图书分类统计放在同一行 -->
    <el-row :gutter="20" class="category-row">
      <el-col :sm="24" :md="12" class="category-col notice-col notice-row">
        <LibraryNotices class="uniform-card" />
      </el-col>
      <el-col :sm="24" :md="12" class="category-col chart-col notice-row">
        <BookCategoryChart class="uniform-card" />
      </el-col>
    </el-row>

    <!-- 图书馆借阅趋势 -->
    <el-row :gutter="20">
      <el-col :sm="24">
        <BookBorrowTrend />
      </el-col>
    </el-row>

    <AboutLibrary />
  </div>
</template>

<script setup lang="ts">
  import BookStatistics from './widget/BookStatistics.vue'
  import BookBorrowTrend from './widget/BookBorrowTrend.vue'
  import BookCategoryChart from './widget/BookCategoryChart.vue'
  import LibraryNotices from './widget/LibraryNotices.vue'
  // import TodoList from './widget/TodoList.vue'
  import AboutLibrary from './widget/AboutLibrary.vue'
  // import QuickActions from './widget/QuickActions.vue'
  // import { useCommon } from '@/composables/useCommon'

  defineOptions({ name: 'LibraryDashboard' })

  // useCommon().scrollToTop()
</script>

<style lang="scss" scoped>
  @use './style';
  .uniform-card {
    height: 100%;
    min-height: 300px; // 根据实际情况调整高度值
  }

  .category-row {
    margin-bottom: 20px;
  }

  .notice-row {
    margin-top: 20px;
  }
</style>
