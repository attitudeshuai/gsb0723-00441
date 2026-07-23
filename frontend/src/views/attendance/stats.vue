<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { getAttendanceStats } from '@/api/attendance'
import { getAllClasses } from '@/api/class'
import { formatDate } from '@/utils/format'
import * as echarts from 'echarts'
import dayjs from 'dayjs'

const loading = ref(false)
const classList = ref([])
const statsData = ref(null)

const queryParams = reactive({
  classId: '',
  startDate: dayjs().subtract(30, 'day').format('YYYY-MM-DD'),
  endDate: dayjs().format('YYYY-MM-DD')
})

const trendChart = ref(null)
const classChart = ref(null)
let trendInstance = null
let classInstance = null

onMounted(async () => {
  await loadClasses()
  initCharts()
  loadStats()
})

async function loadClasses() {
  try {
    classList.value = await getAllClasses()
  } catch {
    classList.value = []
  }
}

function initCharts() {
  trendInstance = echarts.init(trendChart.value)
  classInstance = echarts.init(classChart.value)

  window.addEventListener('resize', () => {
    trendInstance?.resize()
    classInstance?.resize()
  })
}

watch(() => [queryParams.classId, queryParams.startDate, queryParams.endDate], () => {
  loadStats()
})

async function loadStats() {
  loading.value = true
  try {
    statsData.value = await getAttendanceStats(queryParams)
    renderCharts()
  } catch {
    statsData.value = null
  } finally {
    loading.value = false
  }
}

function renderCharts() {
  if (!statsData.value) return

  // 出勤率趋势图
  const trend = statsData.value.dailyTrend || []

  trendInstance?.setOption({
    title: { text: '出勤率趋势', left: 'center' },
    tooltip: { trigger: 'axis', formatter: '{b}<br/>出勤率: {c}%' },
    xAxis: {
      type: 'category',
      data: trend.map(t => formatDate(t.date))
    },
    yAxis: {
      type: 'value',
      max: 100,
      name: '出勤率(%)'
    },
    series: [{
      type: 'line',
      data: trend.map(t => (t.attendanceRate * 100).toFixed(1)),
      smooth: true,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(59, 130, 246, 0.3)' }, // Blue 500
          { offset: 1, color: 'rgba(59, 130, 246, 0.05)' }
        ])
      },
      lineStyle: { color: '#3B82F6', width: 2 },
      itemStyle: { color: '#3B82F6' }
    }]
  })

  // 班级出勤率对比
  const classStats = statsData.value.classStats || []

  classInstance?.setOption({
    title: { text: '班级出勤率对比', left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: classStats.map(c => c.className),
      axisLabel: { rotate: 30 }
    },
    yAxis: {
      type: 'value',
      max: 100,
      name: '出勤率(%)'
    },
    series: [{
      type: 'bar',
      data: classStats.map(c => ({
        value: (c.attendanceRate * 100).toFixed(1),
        itemStyle: {
          color: c.attendanceRate >= 0.95 ? '#10B981' : c.attendanceRate >= 0.9 ? '#3B82F6' : '#EF4444'
        }
      })),
      label: { show: true, position: 'top', formatter: '{c}%' }
    }]
  })
}
</script>

<template>
  <div class="page-container" v-loading="loading">
    <div class="search-form">
      <el-form inline>
        <el-form-item label="班级">
          <el-select v-model="queryParams.classId" placeholder="全部班级" clearable style="width: 160px">
            <el-option v-for="item in classList" :key="item.id" :label="item.className" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="queryParams.startDate"
            type="date"
            placeholder="开始日期"
            value-format="YYYY-MM-DD"
            style="width: 140px"
          />
          <span style="margin: 0 8px">至</span>
          <el-date-picker
            v-model="queryParams.endDate"
            type="date"
            placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 140px"
          />
        </el-form-item>
      </el-form>
    </div>

    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <div class="stat-card stat-card--blue">
          <div class="stat-value">{{ statsData?.totalDays || 0 }}</div>
          <div class="stat-label">统计天数</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card--green">
          <div class="stat-value">{{ statsData?.avgAttendanceRate ? (statsData.avgAttendanceRate * 100).toFixed(1) + '%' : '-' }}</div>
          <div class="stat-label">平均出勤率</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card--orange">
          <div class="stat-value">{{ statsData?.absentCount || 0 }}</div>
          <div class="stat-label">缺勤人次</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card--red">
          <div class="stat-value">{{ statsData?.lateCount || 0 }}</div>
          <div class="stat-label">迟到人次</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <div ref="trendChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div ref="classChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="detail-card">
      <template #header>异常考勤记录</template>
      <el-table :data="statsData?.abnormalRecords || []" border stripe max-height="300">
        <el-table-column prop="attendDate" label="日期" width="120">
          <template #default="{ row }">{{ formatDate(row.attendDate) }}</template>
        </el-table-column>
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="studentName" label="学生" width="100" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 2 ? 'danger' : row.status === 3 ? 'warning' : 'info'" size="small">
              {{ row.status === 2 ? '缺勤' : row.status === 3 ? '迟到' : '请假' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" />
      </el-table>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
@use "@/assets/styles/variables.scss" as *;

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  padding: 24px;
  border-radius: $border-radius-base;
  background: #fff;
  border: 1px solid $border-light;
  text-align: center;
  box-shadow: $box-shadow-light;

  &--blue {
    .stat-value { color: $primary-accent; }
  }

  &--green {
    .stat-value { color: $success-color; }
  }

  &--orange {
    .stat-value { color: $warning-color; }
  }

  &--red {
    .stat-value { color: $danger-color; }
  }

  .stat-value {
    font-size: 32px;
    font-weight: 600;
    margin-bottom: 8px;
    font-family: 'Inter', sans-serif;
  }

  .stat-label {
    font-size: 14px;
    color: $text-secondary;
  }
}

.chart-container {
  height: 350px;
}

.detail-card {
  margin-top: 20px;
}
</style>
