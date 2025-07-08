<!-- BookCategoryChart.vue -->
<template>
  <div class="card art-custom-card">
    <div class="card-header">
      <div class="title">
        <h3 class="box-title">图书分类统计</h3>
        <p class="subtitle">本月新增<span class="text-success">+125册</span></p>
      </div>
    </div>
    <div class="chart" ref="chartRef"></div>
  </div>
</template>

<script setup lang="ts">
  import * as echarts from 'echarts'
  import { hexToRgba, getCssVar } from '@/utils/ui'
  import { EChartsOption } from 'echarts'
  import { useChart } from '@/composables/useChart'
  import { ref, onMounted, watch } from 'vue'
  import { getLibraryStats } from '@/api/libraryStatsApi' // 新增

  const {
    chartRef,
    isDark,
    initChart,
    updateChart,
    getAxisLabelStyle,
    getAxisLineStyle,
    getAxisTickStyle,
    getSplitLineStyle
  } = useChart()

  const realData = ref<number[]>([])
  const categoryNames = ref<string[]>([])

  const fetchCategoryData = async () => {
    const res = await getLibraryStats()
    if (res.code === 200 && res.data && res.data.bookCategoryCount) {
      // bookCategoryCount: { "教育": 3, ... }
      categoryNames.value = Object.keys(res.data.bookCategoryCount)
      realData.value = Object.values(res.data.bookCategoryCount)
    } else {
      // 若无数据，给默认值
      categoryNames.value = []
      realData.value = []
    }
  }

  const initChartWithAnimation = async () => {
    await fetchCategoryData()
    initChart(options(true))
    updateChart(options(false))
  }

  watch(isDark, () => {
    initChart(options())
  })

  onMounted(() => {
    initChartWithAnimation()
  })

  const options: (isInitial?: boolean) => EChartsOption = (isInitial) => {
    const isInit = isInitial || false
    return {
      animation: true,
      animationDuration: 0,
      animationDurationUpdate: 0,
      grid: {
        left: '2.2%',
        right: '3%',
        bottom: '0%',
        top: '5px',
        containLabel: true
      },
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: categoryNames.value,
        axisTick: getAxisTickStyle(),
        axisLabel: getAxisLabelStyle(true),
        axisLine: getAxisLineStyle(true)
      },
      yAxis: {
        type: 'value',
        min: 0,
        max: realData.value.reduce((prev, curr) => Math.max(prev, curr), 0),
        axisLabel: getAxisLabelStyle(true),
        axisLine: getAxisLineStyle(!isDark.value),
        splitLine: getSplitLineStyle(true)
      },
      series: [
        {
          name: '图书数量',
          color: getCssVar('--main-color'),
          type: 'line',
          stack: '总量',
          data: isInit ? new Array(categoryNames.value.length).fill(0) : realData.value,
          smooth: true,
          symbol: 'none',
          lineStyle: {
            width: 2.2
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: hexToRgba(getCssVar('--el-color-primary'), 0.15).rgba
              },
              {
                offset: 1,
                color: hexToRgba(getCssVar('--el-color-primary'), 0.01).rgba
              }
            ])
          },
          animationDuration: 0,
          animationDurationUpdate: 1500
        }
      ]
    }
  }
</script>
