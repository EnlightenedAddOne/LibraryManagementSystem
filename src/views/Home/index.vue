<template>
  <div class="console">
    <BookStatistics></BookStatistics>

    <el-row :gutter="20">
      <el-col :sm="24" :md="12" :lg="10">
        <BookBorrowTrend />
      </el-col>
      <el-col :sm="24" :md="12" :lg="14">
        <BookCategoryChart />
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :sm="24" :md="24" :lg="12">
        <RecentReaders />
      </el-col>
      <el-col :sm="24" :md="12" :lg="6">
        <LibraryNotices />
      </el-col>
      <el-col :sm="24" :md="12" :lg="6">
        <TodoList />
      </el-col>
    </el-row>

    <AboutLibrary />
  </div>
</template>

<script setup lang="ts">
  import BookStatistics from './widget/BookStatistics.vue'
  import BookBorrowTrend from './widget/BookBorrowTrend.vue'
  import BookCategoryChart from './widget/BookCategoryChart.vue'
  import RecentReaders from './widget/RecentReaders.vue'
  import LibraryNotices from './widget/LibraryNotices.vue'
  import TodoList from './widget/TodoList.vue'
  import AboutLibrary from './widget/AboutLibrary.vue'
  import { useCommon } from '@/composables/useCommon'

  defineOptions({ name: 'LibraryDashboard' })

  useCommon().scrollToTop()
</script>

<style lang="scss" scoped>
  @use './style';
</style>
