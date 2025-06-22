<!-- TodoList.vue -->
<template>
  <div class="card art-custom-card">
    <div class="card-header">
      <div class="title">
        <h4 class="box-title">待办事项</h4>
        <p class="subtitle">待处理<span class="text-danger">4</span></p>
      </div>
    </div>

    <div class="list">
      <div v-for="(item, index) in list" :key="index">
        <p class="title">{{ item.task }}</p>
        <p class="date subtitle">{{ item.date }}</p>
        <el-checkbox v-model="item.completed" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { reactive } from 'vue-demi'

  const list = reactive([
    {
      task: '处理新书入库申请',
      date: '上午 09:00',
      completed: true
    },
    {
      task: '审核读者注册信息',
      date: '上午 10:30',
      completed: true
    },
    {
      task: '整理过期图书清单',
      date: '上午 11:30',
      completed: false
    },
    {
      task: '图书馆月度统计报告',
      date: '下午 02:00',
      completed: false
    },
    {
      task: '检查图书损坏情况',
      date: '下午 03:30',
      completed: false
    },
    {
      task: '更新图书分类标签',
      date: '下午 04:30',
      completed: false
    }
  ])
</script>
