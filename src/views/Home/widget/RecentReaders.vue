<!-- RecentReaders.vue -->
<template>
  <div class="card art-custom-card">
    <div class="card-header">
      <div class="title">
        <h4 class="box-title">新注册读者</h4>
        <p class="subtitle">本月新增<span class="text-success">+68人</span></p>
      </div>
      <el-radio-group v-model="radio2">
        <el-radio-button value="本月" label="本月"></el-radio-button>
        <el-radio-button value="上月" label="上月"></el-radio-button>
        <el-radio-button value="今年" label="今年"></el-radio-button>
      </el-radio-group>
    </div>
    <art-table
      class="table"
      :data="tableData"
      :pagination="false"
      size="large"
      :border="false"
      :stripe="false"
      :show-header-background="false"
    >
      <template #default>
        <el-table-column label="读者" prop="avatar" width="150px">
          <template #default="scope">
            <div style="display: flex; align-items: center">
              <img class="avatar" :src="scope.row.avatar" />
              <span class="user-name">{{ scope.row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="读者类型" prop="readerType" />
        <el-table-column label="专业" prop="major" />
        <el-table-column label="借阅活跃度" width="240">
          <template #default="scope">
            <el-progress :percentage="scope.row.pro" :color="scope.row.color" :stroke-width="4" />
          </template>
        </el-table-column>
      </template>
    </art-table>
  </div>
</template>

<script setup lang="ts">
  import { onMounted, ref, reactive } from 'vue-demi'
  import avatar1 from '@/assets/img/avatar/avatar1.webp'
  import avatar2 from '@/assets/img/avatar/avatar2.webp'
  import avatar3 from '@/assets/img/avatar/avatar3.webp'
  import avatar4 from '@/assets/img/avatar/avatar4.webp'
  import avatar5 from '@/assets/img/avatar/avatar5.webp'
  import avatar6 from '@/assets/img/avatar/avatar6.webp'

  const radio2 = ref('本月')

  const tableData = reactive([
    {
      username: '张小明',
      readerType: '学生',
      major: '计算机科学',
      percentage: 85,
      pro: 0,
      color: 'rgb(var(--art-primary)) !important',
      avatar: avatar1
    },
    {
      username: '李小花',
      readerType: '教师',
      major: '文学',
      percentage: 92,
      pro: 0,
      color: 'rgb(var(--art-success)) !important',
      avatar: avatar2
    },
    {
      username: '王小强',
      readerType: '学生',
      major: '物理学',
      percentage: 73,
      pro: 0,
      color: 'rgb(var(--art-warning)) !important',
      avatar: avatar3
    },
    {
      username: '陈小雨',
      readerType: '研究生',
      major: '化学',
      percentage: 68,
      pro: 0,
      color: 'rgb(var(--art-info)) !important',
      avatar: avatar4
    },
    {
      username: '刘小雪',
      readerType: '学生',
      major: '历史学',
      percentage: 79,
      pro: 0,
      color: 'rgb(var(--art-secondary)) !important',
      avatar: avatar5
    },
    {
      username: '赵小风',
      readerType: '博士生',
      major: '生物学',
      percentage: 95,
      pro: 0,
      color: 'rgb(var(--art-error)) !important',
      avatar: avatar6
    }
  ])

  onMounted(() => {
    addAnimation()
  })

  const addAnimation = () => {
    setTimeout(() => {
      for (let i = 0; i < tableData.length; i++) {
        let item = tableData[i]
        tableData[i].pro = item.percentage
      }
    }, 100)
  }
</script>
