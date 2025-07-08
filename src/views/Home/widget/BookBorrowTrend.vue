<!-- BookBorrowTrend.vue -->
<template>
  <div class="card art-custom-card book-borrow-trend-card">
    <div class="content-container">
      <!-- 图表区域 -->
      <div class="chart-wrapper">
        <div class="text">
          <h3 class="box-title">借阅趋势</h3>
          <p class="subtitle">比上月 <span class="text-success">+12%</span></p>
          <p class="subtitle">显示了图书馆近期的借阅量变化趋势，帮助了解读者活跃度</p>
        </div>
        <div class="chart" ref="chartRef"></div>
      </div>

      <!-- 统计数据区域（移至右侧） -->
      <div class="stats-list">
        <div v-for="(item, index) in statsList" :key="index" class="stat-item">
          <p class="num">{{ item.num }}</p>
          <p class="name">{{ item.name }}</p>
        </div>
      </div>
    </div>
    <!-- 装饰图片 -->
    <div class="image-container">
      <img
        src="@/assets/img/home_card_bg/卡提西亚.png"
        alt="文学少女"
        class="trend-bg-image"
        loading="lazy"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
  import * as echarts from 'echarts'
  import { getCssVar } from '@/utils/ui'
  import { useChart } from '@/composables/useChart'
  import { EChartsOption } from 'echarts'
  import { ref, onMounted, watch } from 'vue'
  import { getLibraryStats } from '@/api/libraryStatsApi'

  const {
    chartRef,
    isDark,
    initChart,
    getAxisLineStyle,
    getAxisLabelStyle,
    getAxisTickStyle,
    getSplitLineStyle
  } = useChart()

  // 右侧统计数据
  const statsList = ref([
    { name: '总借阅量', num: '--' },
    { name: '在借图书', num: '--' },
    { name: '日均借阅', num: '--' },
    { name: '归还率', num: '--' }
  ])

  // 图表数据
  const xAxisLabels = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  const barData = ref<number[]>([0, 0, 0, 0, 0, 0, 0])

  const fetchData = async () => {
    const res = await getLibraryStats()
    if (res.code === 200 && res.data) {
      // 右侧统计数据
      statsList.value = [
        { name: '总借阅量', num: `${res.data.totalBorrowCount ?? '--'}` },
        { name: '在借图书', num: `${res.data.currentBorrowingCount ?? '--'}` },
        { name: '日均借阅', num: `${res.data.lastWeekAvgBorrow ?? '--'}` },
        { name: '归还率', num: `${res.data.lastWeekReturnRate ?? '--'}` }
      ]
      // 图表数据处理
      // lastWeekBorrowCountPerDay: { "2025-06-30": 0, ... }
      const dayMap = res.data.lastWeekBorrowCountPerDay || {}
      // 获取日期并排序
      const days = Object.keys(dayMap).sort()
      // 只取前7天，按顺序映射到周一~周日
      barData.value = days.slice(0, 7).map((d) => dayMap[d])
    }
  }

  const options: () => EChartsOption = () => {
    return {
      grid: {
        top: 15,
        right: 0,
        bottom: 0,
        left: 0,
        containLabel: true
      },
      tooltip: {
        trigger: 'item'
      },
      xAxis: {
        type: 'category',
        data: xAxisLabels,
        axisTick: getAxisTickStyle(),
        axisLine: getAxisLineStyle(true),
        axisLabel: getAxisLabelStyle(true)
      },
      yAxis: {
        axisLabel: getAxisLabelStyle(true),
        axisLine: getAxisLineStyle(!isDark.value),
        splitLine: getSplitLineStyle(true)
      },
      series: [
        {
          data: barData.value,
          type: 'bar',
          itemStyle: {
            borderRadius: 4,
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: getCssVar('--el-color-primary-light-4')
              },
              {
                offset: 1,
                color: getCssVar('--el-color-primary')
              }
            ])
          },
          barWidth: '50%',
          animationDelay: (idx) => idx * 50 + 300,
          animationDuration: (idx) => 1500 - idx * 50,
          animationEasing: 'quarticOut'
        }
      ]
    }
  }

  watch(isDark, () => {
    initChart(options())
  })

  onMounted(async () => {
    await fetchData()
    initChart(options())
  })
</script>

<style lang="scss" scoped>
  .card {
    display: flex;
    flex-direction: column;
    padding: 20px;
    background-color: var(--art-main-bg-color);
    border: 1px solid var(--el-border-color-lighter);
    border-radius: var(--custom-radius);

    .chart {
      height: 200px;
      margin-bottom: 16px;
    }

    .text {
      margin-bottom: 20px;

      h3.box-title {
        margin-bottom: 8px;
        font-size: 18px;
        font-weight: 600;
        color: var(--art-gray-900);
      }

      p.subtitle {
        margin: 4px 0;
        font-size: 13px;
        color: var(--art-gray-600);

        span.text-success {
          font-weight: 500;
          color: var(--el-color-success);
        }
      }

      // 限制描述行宽，提升可读性
      p.subtitle:last-child {
        max-width: 360px;
        line-height: 1.4;
      }
    }

    .list {
      display: flex;
      gap: 12px;
      justify-content: space-between;

      div {
        flex: 1;
        padding: 10px;
        text-align: center;
        background: var(--el-color-primary-light-9);
        border-radius: 8px;

        p:first-child {
          margin-bottom: 6px;
          font-size: 20px;
          font-weight: bold;
          color: var(--el-color-primary);
        }

        p.subtitle {
          margin: 0;
          font-size: 13px;
          color: var(--art-gray-600);
        }
      }
    }
  }

  .content-container {
    display: flex;
    gap: 20px;
    width: 100%;
  }

  .chart-wrapper {
    flex: 2; // 图表区域占据2/3宽度
  }

  .stats-list {
    display: flex;
    flex: 1; // 统计数据区域占据1/3宽度
    flex-direction: column;
    gap: 20px;
    justify-content: center;
    margin-left: -110px; // 向左移动20px，数值可根据实际效果调整
  }

  .stat-item {
    padding: 15px 10px;
    text-align: center;
    background-color: rgb(255 255 255 / 5%);
    border-radius: 8px;
  }

  .num {
    margin-bottom: 5px;
    font-size: 24px;
    font-weight: bold;
  }

  .name {
    font-size: 14px;
    color: #666;
  }

  // 响应式调整
  @media (width <= 768px) {
    .content-container {
      flex-direction: column;
    }

    .stats-list {
      flex-flow: row wrap;
    }

    .stat-item {
      flex: 1 0 45%;
    }
  }

  .image-container {
    position: absolute;
    right: 0;
    bottom: 25px;
    z-index: 1;
    display: flex;
    align-items: flex-end;
    justify-content: flex-end;
    width: 40%;
    height: 90%;
    pointer-events: none;
    opacity: 0.6; // 可根据需要调整
  }

  .trend-bg-image {
    display: block;
    max-width: 100%;
    max-height: 100%;
    object-fit: contain;
  }
</style>
