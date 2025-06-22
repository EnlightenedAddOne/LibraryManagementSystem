<!-- BookBorrowTrend.vue -->
<template>
  <div class="card art-custom-card">
    <div class="chart" ref="chartRef"></div>
    <div class="text">
      <h3 class="box-title">借阅趋势</h3>
      <p class="subtitle">比上月 <span class="text-success">+12%</span></p>
      <p class="subtitle">显示了图书馆近期的借阅量变化趋势，帮助了解读者活跃度</p>
    </div>
    <div class="list">
      <div v-for="(item, index) in list" :key="index">
        <p>{{ item.num }}</p>
        <p class="subtitle">{{ item.name }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import * as echarts from 'echarts'
  import { getCssVar } from '@/utils/ui'
  import { useChart } from '@/composables/useChart'
  import { EChartsOption } from 'echarts'

  const {
    chartRef,
    isDark,
    initChart,
    getAxisLineStyle,
    getAxisLabelStyle,
    getAxisTickStyle,
    getSplitLineStyle
  } = useChart()

  const list = [
    { name: '总借阅量', num: '8.2k' },
    { name: '在借图书', num: '2.1k' },
    { name: '日均借阅', num: '105' },
    { name: '归还率', num: '98%' }
  ]

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
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日', '本周', '预测'],
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
          data: [120, 98, 135, 110, 158, 95, 142, 128, 145],
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

  onMounted(() => {
    initChart(options())
  })
</script>

<style lang="scss" scoped>
  .card {
    display: flex;
    flex-direction: column;
    padding: 20px;
    background-color: var(--art-main-bg-color);
    border-radius: var(--custom-radius);
    border: 1px solid var(--el-border-color-lighter);

    .chart {
      height: 200px;
      margin-bottom: 16px;
    }

    .text {
      margin-bottom: 20px;

      h3.box-title {
        font-size: 18px;
        font-weight: 600;
        color: var(--art-gray-900);
        margin-bottom: 8px;
      }

      p.subtitle {
        font-size: 13px;
        color: var(--art-gray-600);
        margin: 4px 0;

        span.text-success {
          color: var(--el-color-success);
          font-weight: 500;
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
      justify-content: space-between;
      gap: 12px;

      div {
        flex: 1;
        text-align: center;
        background: var(--el-color-primary-light-9);
        padding: 10px;
        border-radius: 8px;

        p:first-child {
          font-size: 20px;
          font-weight: bold;
          color: var(--el-color-primary);
          margin-bottom: 6px;
        }

        p.subtitle {
          font-size: 13px;
          color: var(--art-gray-600);
          margin: 0;
        }
      }
    }
  }
</style>
