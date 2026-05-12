<script setup lang="ts">
import { computed } from 'vue'
import { use } from 'echarts/core'
import { BarChart } from 'echarts/charts'
import {
  GridComponent,
  LegendComponent,
  TooltipComponent,
  type GridComponentOption,
  type LegendComponentOption,
  type TooltipComponentOption,
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import VChart from 'vue-echarts'
import type { ComposeOption } from 'echarts/core'
import type { BarSeriesOption } from 'echarts/charts'

import type { AdminMonthlyStats } from '../../types/dashboard'

use([CanvasRenderer, BarChart, GridComponent, LegendComponent, TooltipComponent])

type EChartsOption = ComposeOption<
  BarSeriesOption | GridComponentOption | LegendComponentOption | TooltipComponentOption
>

const props = defineProps<{
  stats: AdminMonthlyStats[]
  loading?: boolean
  error?: string
}>()

const emit = defineEmits<{
  retry: []
}>()

function formatMonth(month: string) {
  const [year, monthNumber] = month.split('-')
  const normalizedMonth = Number(monthNumber)

  if (!year || Number.isNaN(normalizedMonth)) {
    return month
  }

  return `T${normalizedMonth}/${year}`
}

function formatCompactValue(value: number) {
  if (Math.abs(value) >= 1000) {
    return new Intl.NumberFormat('vi-VN', {
      notation: 'compact',
      compactDisplay: 'short',
      maximumFractionDigits: 1,
    }).format(value)
  }

  return value.toLocaleString('vi-VN')
}

const chartOption = computed<EChartsOption>(() => {
  const axisLabels = props.stats.map((item) => formatMonth(item.month))

  return {
    animationDuration: 700,
    animationEasing: 'cubicOut',
    color: ['#6b4f3f', '#c98b47', '#9f6c3d', '#6f8f7a', '#b65c4b'],
    grid: {
      left: 8,
      right: 12,
      top: 52,
      bottom: 8,
      containLabel: true,
    },
    legend: {
      top: 0,
      left: 0,
      icon: 'roundRect',
      itemWidth: 12,
      itemHeight: 12,
      textStyle: {
        color: '#5f5147',
        fontSize: 12,
      },
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow',
        shadowStyle: {
          color: 'rgba(107, 79, 63, 0.08)',
        },
      },
      backgroundColor: 'rgba(255, 250, 244, 0.96)',
      borderColor: 'rgba(124, 90, 63, 0.18)',
      borderWidth: 1,
      textStyle: {
        color: '#34261d',
      },
      valueFormatter: (value) =>
        typeof value === 'number' ? value.toLocaleString('vi-VN') : String(value),
    },
    xAxis: {
      type: 'category',
      data: axisLabels,
      axisTick: {
        show: false,
      },
      axisLine: {
        lineStyle: {
          color: 'rgba(117, 83, 57, 0.18)',
        },
      },
      axisLabel: {
        color: '#5f5147',
        fontSize: 12,
      },
    },
    yAxis: {
      type: 'value',
      splitNumber: 4,
      axisLabel: {
        color: '#7d6a5d',
        formatter: (value: number) => formatCompactValue(value),
      },
      splitLine: {
        lineStyle: {
          color: 'rgba(117, 83, 57, 0.1)',
        },
      },
    },
    series: [
      {
        name: 'Người dùng mới',
        type: 'bar',
        barMaxWidth: 18,
        emphasis: { focus: 'series' },
        data: props.stats.map((item) => item.newUsers),
      },
      {
        name: 'Sách mới',
        type: 'bar',
        barMaxWidth: 18,
        emphasis: { focus: 'series' },
        data: props.stats.map((item) => item.newBooks),
      },
      {
        name: 'Chương mới',
        type: 'bar',
        barMaxWidth: 18,
        emphasis: { focus: 'series' },
        data: props.stats.map((item) => item.newChapters),
      },
      {
        name: 'Lượt đọc',
        type: 'bar',
        barMaxWidth: 18,
        emphasis: { focus: 'series' },
        data: props.stats.map((item) => item.views),
      },
      {
        name: 'Bình luận',
        type: 'bar',
        barMaxWidth: 18,
        emphasis: { focus: 'series' },
        data: props.stats.map((item) => item.comments),
      },
    ],
  }
})
</script>

<template>
  <div class="dashboard-bar-chart">
    <div v-if="loading" class="dashboard-bar-chart__loading" aria-live="polite">
      <p class="dashboard-bar-chart__loading-copy">Đang tải dữ liệu...</p>
      <div class="dashboard-bar-chart__skeleton dashboard-bar-chart__skeleton--header"></div>
      <div class="dashboard-bar-chart__skeleton dashboard-bar-chart__skeleton--plot"></div>
    </div>

    <div v-else-if="error" class="dashboard-bar-chart__state dashboard-bar-chart__state--error">
      <p>{{ error }}</p>
      <button type="button" @click="emit('retry')">Thử lại</button>
    </div>

    <div v-else-if="!stats.length" class="dashboard-bar-chart__state">
      <p>Không có dữ liệu</p>
    </div>

    <VChart
      v-else
      class="dashboard-bar-chart__canvas"
      :option="chartOption"
      autoresize
    />
  </div>
</template>

<style scoped>
.dashboard-bar-chart {
  min-height: 23rem;
}

.dashboard-bar-chart__canvas {
  width: 100%;
  min-height: 23rem;
}

.dashboard-bar-chart__loading {
  display: grid;
  gap: 1rem;
}

.dashboard-bar-chart__loading-copy {
  color: #5f5147;
}

.dashboard-bar-chart__skeleton {
  border-radius: 1.15rem;
  background:
    linear-gradient(90deg, rgba(255, 255, 255, 0.54), rgba(255, 255, 255, 0.92), rgba(255, 255, 255, 0.54)),
    rgba(255, 244, 229, 0.8);
  background-size: 220% 100%;
  animation: chart-skeleton-shift 1.2s linear infinite;
}

.dashboard-bar-chart__skeleton--header {
  height: 2.6rem;
}

.dashboard-bar-chart__skeleton--plot {
  min-height: 19rem;
}

.dashboard-bar-chart__state {
  display: grid;
  place-items: center;
  gap: 0.9rem;
  min-height: 23rem;
  padding: 2rem;
  border: 1px dashed rgba(117, 83, 57, 0.2);
  border-radius: 1.35rem;
  background: rgba(255, 251, 245, 0.86);
  text-align: center;
  color: #5f5147;
}

.dashboard-bar-chart__state--error {
  background: rgba(166, 85, 61, 0.06);
  color: #7d3f31;
}

.dashboard-bar-chart__state button {
  min-height: 2.85rem;
  padding: 0.7rem 1.1rem;
  border-radius: 999px;
  background: linear-gradient(135deg, #c98b47, #8d5b33);
  color: white;
  font-weight: 600;
  transition:
    transform 180ms ease-out,
    box-shadow 180ms ease-out;
}

.dashboard-bar-chart__state button:hover {
  transform: translateY(-1px);
  box-shadow: 0 14px 26px rgba(141, 91, 51, 0.22);
}

@keyframes chart-skeleton-shift {
  from {
    background-position: 0 0;
  }

  to {
    background-position: 220% 0;
  }
}
</style>
