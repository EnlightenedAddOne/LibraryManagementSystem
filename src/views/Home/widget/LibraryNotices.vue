<!-- LibraryNotices.vue -->
<template>
  <div class="card art-custom-card">
    <div class="card-header">
      <div class="title">
        <h4 class="box-title">图书馆公告</h4>
        <p class="subtitle">最新<span class="text-success">3条</span></p>
      </div>
    </div>

    <div class="list">
      <div v-for="(item, index) in list" :key="index">
        <span class="user">{{ item.title }}</span>
        <span class="type">{{ item.type }}</span>
        <span class="target">{{ item.content }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { reactive } from 'vue-demi'

  const list = reactive([
    {
      title: '系统升级',
      type: '通知',
      content: '图书管理系统将于本周末进行升级维护'
    },
    {
      title: '新书上架',
      type: '资讯',
      content: '本月新增科技类图书180册'
    },
    {
      title: '借阅规则',
      type: '提醒',
      content: '请按时归还图书，逾期将产生滞纳金'
    },
    {
      title: '活动通知',
      type: '活动',
      content: '读书分享会将于下周三举行'
    },
    {
      title: '开放时间',
      type: '通知',
      content: '节假日期间图书馆开放时间调整'
    },
    {
      title: '数据库更新',
      type: '系统',
      content: '电子资源数据库已更新至最新版本'
    }
  ])
</script>
