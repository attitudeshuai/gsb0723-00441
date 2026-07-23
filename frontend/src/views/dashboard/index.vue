<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDashboardStats } from '@/api/dashboard'
import { getNoticeList } from '@/api/notice'
import { formatDateTime, formatNoticeType } from '@/utils/format'

const stats = ref({
  studentCount: 0,
  teacherCount: 0,
  classCount: 0,
  todayAttendanceRate: 0,
  studentTrend: 0,
  teacherTrend: 0,
  attendanceTrend: 0,
  scoreDistribution: [],
  attendanceTrendData: []
})

const notices = ref([])
const scoreChartRef = ref()
const attendanceChartRef = ref()

onMounted(async () => {
  await loadData()
  await nextTick()
  initCharts()
})

async function loadData() {
  try {
    const [statsData, noticeData] = await Promise.all([
      getDashboardStats().catch(() => null),
      getNoticeList({ page: 1, size: 4 }).catch(() => ({ records: [] }))
    ])
    if (statsData) stats.value = statsData
    notices.value = noticeData?.records || []
  } catch (err) {}
}

const scoreColors = ['#3B82F6', '#6366F1', '#8B5CF6', '#F43F5E', '#94A3B8']

function initCharts() {
  if (scoreChartRef.value) {
    const chart = echarts.init(scoreChartRef.value)
    const scoreData = (stats.value.scoreDistribution || []).map((item, index) => ({
      value: item.value || 0,
      name: item.name,
      itemStyle: { color: scoreColors[index] || '#94A3B8' }
    }))
    chart.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c}人 ({d}%)',
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        borderRadius: 8,
        borderWidth: 0,
        shadowBlur: 10,
        shadowColor: 'rgba(0,0,0,0.1)'
      },
      legend: {
        orient: 'vertical',
        left: '10%',
        top: 'middle',
        itemGap: 15,
        textStyle: { color: '#64748B', fontSize: 13 },
        icon: 'circle'
      },
      series: [{
        name: '成绩分布',
        type: 'pie',
        radius: ['60%', '80%'],
        center: ['60%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 4 },
        label: { show: false },
        data: scoreData.length > 0 ? scoreData : [{ value: 0, name: '暂无数据' }]
      }]
    })
  }

  if (attendanceChartRef.value) {
    const chart = echarts.init(attendanceChartRef.value)
    const trendData = stats.value.attendanceTrendData || []
    const dates = trendData.map(item => item.date)
    const rates = trendData.map(item => item.rate || 0)

    chart.setOption({
      grid: { top: 10, bottom: 20, left: 30, right: 10 },
      xAxis: {
        type: 'category',
        data: dates.length > 0 ? dates : ['暂无数据'],
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: { color: '#94A3B8', fontSize: 11 }
      },
      yAxis: {
        type: 'value',
        min: 0,
        max: 100,
        splitLine: { lineStyle: { color: '#F1F5F9', type: 'dashed' } },
        axisLabel: { show: false }
      },
      series: [{
        data: rates.length > 0 ? rates : [0],
        type: 'line',
        smooth: 0.4,
        showSymbol: false,
        lineStyle: { width: 4, color: '#3B82F6' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(59, 130, 246, 0.15)' },
            { offset: 1, color: 'rgba(59, 130, 246, 0)' }
          ])
        }
      }]
    })
  }
}

function formatTrend(value) {
  if (value === 0 || value === null || value === undefined) return '0'
  const num = Number(value)
  return num > 0 ? `+${num}` : `${num}`
}
</script>

<template>
  <div class="dashboard-v4">
    <!-- Bento Grid -->
    <div class="grid-layout">
      <!-- Row 1: KPI Cards -->
      <div class="grid-row stats-row">
        <div class="bento-card kpi">
          <div class="kpi-icon bg-blue-50 text-blue-500"><el-icon><User /></el-icon></div>
          <div class="kpi-body">
            <span class="label">在校学生</span>
            <div class="val-group">
              <span class="value">{{ stats.studentCount }}</span>
              <span class="trend" :class="stats.studentTrend >= 0 ? 'up' : 'down'">{{ formatTrend(stats.studentTrend) }}%</span>
            </div>
          </div>
        </div>

        <div class="bento-card kpi">
          <div class="kpi-icon bg-indigo-50 text-indigo-500"><el-icon><UserFilled /></el-icon></div>
          <div class="kpi-body">
            <span class="label">教职员工</span>
            <div class="val-group">
              <span class="value">{{ stats.teacherCount }}</span>
              <span class="trend" :class="stats.teacherTrend >= 0 ? 'up' : 'down'">{{ formatTrend(stats.teacherTrend) }}</span>
            </div>
          </div>
        </div>

        <div class="bento-card kpi">
          <div class="kpi-icon bg-emerald-50 text-emerald-500"><el-icon><Calendar /></el-icon></div>
          <div class="kpi-body">
            <span class="label">今日出勤</span>
            <div class="val-group">
              <span class="value">{{ stats.todayAttendanceRate || 0 }}<small>%</small></span>
              <span class="trend" :class="stats.attendanceTrend >= 0 ? 'up' : 'down'">{{ formatTrend(stats.attendanceTrend) }}%</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Row 2: Charts & Notices -->
      <div class="grid-row main-row">
        <div class="bento-card chart-box">
          <div class="card-head">
            <h3>成绩分布分析</h3>
            <span class="tag-label blue">本学期</span>
          </div>
          <div ref="scoreChartRef" class="chart-inner"></div>
        </div>

        <div class="bento-card notice-box">
          <div class="card-head">
            <h3>通知公告</h3>
            <button class="text-btn" @click="$router.push('/notice')">查看全部</button>
          </div>
          <div class="notice-items">
            <div v-for="n in notices" :key="n.id" class="n-item">
              <div class="n-dot" :class="{ urgent: n.type === 3 }"></div>
              <div class="n-info">
                <span class="n-title">{{ n.title }}</span>
                <span class="n-time">{{ formatDateTime(n.publishTime) }}</span>
              </div>
            </div>
            <el-empty v-if="!notices.length" :image-size="60" description="暂无公告" />
          </div>
        </div>
      </div>

      <!-- Row 3: Trend -->
      <div class="grid-row bottom-row">
        <div class="bento-card trend-box">
          <div class="card-head">
            <h3>全校考勤趋势</h3>
            <div class="legend-simple">
              <span class="dot blue"></span> 正常率 (%)
            </div>
          </div>
          <div ref="attendanceChartRef" class="chart-inner-wide"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.dashboard-v4 {
  min-height: calc(100vh - 64px);
}

.grid-layout {
  display: flex; flex-direction: column; gap: 24px;
}

.grid-row { display: flex; gap: 24px; width: 100%; }

.bento-card {
  background: #fff; border-radius: 24px; border: 1px solid #F1F5F9;
  box-shadow: 0 1px 3px rgba(0,0,0,0.02);
  transition: all 0.3s ease;
  &:hover { transform: translateY(-2px); box-shadow: 0 12px 20px -5px rgba(0,0,0,0.05); }
}

/* KPI Card Styles */
.stats-row .kpi {
  flex: 1; padding: 24px; display: flex; align-items: center; gap: 20px;
  .kpi-icon {
    width: 60px; height: 60px; border-radius: 18px;
    display: flex; align-items: center; justify-content: center; font-size: 26px;
  }
  .label { font-size: 14px; font-weight: 600; color: #64748B; margin-bottom: 4px; display: block; }
  .val-group {
    display: flex; align-items: baseline; gap: 8px;
    .value { font-size: 30px; font-weight: 800; color: #0F172A; letter-spacing: -1px; }
    small { font-size: 16px; font-weight: 600; margin-left: 2px; }
    .trend {
      font-size: 11px; font-weight: 700;
      &.up { color: #10B981; }
      &.down { color: #F43F5E; }
    }
  }
}

/* Main Content Row */
.main-row .chart-box { flex: 1.6; padding: 24px; }
.main-row .notice-box { flex: 1; padding: 24px; }

.card-head {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;
  h3 { font-size: 17px; font-weight: 700; color: #1E293B; }
  .tag-label { font-size: 10px; font-weight: 700; padding: 4px 10px; border-radius: 8px; text-transform: uppercase; }
  .tag-label.blue { background: #EFF6FF; color: #3B82F6; }
  .text-btn { border: none; background: none; color: #3B82F6; font-size: 13px; font-weight: 600; cursor: pointer; &:hover { text-decoration: underline; } }
}

.chart-inner { height: 260px; width: 100%; }
.chart-inner-wide { height: 200px; width: 100%; }

.notice-items {
  display: flex; flex-direction: column; gap: 18px;
  .n-item {
    display: flex; gap: 14px; align-items: flex-start;
    .n-dot { width: 8px; height: 8px; border-radius: 50%; background: #E2E8F0; margin-top: 6px; flex-shrink: 0; }
    .n-dot.urgent { background: #F43F5E; box-shadow: 0 0 8px rgba(244, 63, 94, 0.4); }
    .n-info {
      display: flex; flex-direction: column; gap: 2px;
      .n-title { font-size: 14px; font-weight: 600; color: #334155; line-height: 1.4; }
      .n-time { font-size: 11px; color: #94A3B8; }
    }
  }
}

.bottom-row .trend-box { width: 100%; padding: 24px; }
.legend-simple {
  display: flex; align-items: center; gap: 8px; font-size: 12px; color: #64748B; font-weight: 500;
  .dot { width: 10px; height: 3px; border-radius: 2px; }
  .dot.blue { background: #3B82F6; }
}
</style>
