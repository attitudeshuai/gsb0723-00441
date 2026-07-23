<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { getScoreStats } from '@/api/score'
import { getExamList } from '@/api/exam'
import { getAllClasses } from '@/api/class'
import * as echarts from 'echarts'

const loading = ref(false)
const examList = ref([])
const classList = ref([])
const statsData = ref(null)

const queryParams = reactive({
  examId: '',
  classId: ''
})

const distributionChart = ref(null)
const subjectChart = ref(null)
let distributionInstance = null
let subjectInstance = null

onMounted(async () => {
  await Promise.all([loadExams(), loadClasses()])
  initCharts()
})

async function loadExams() {
  try {
    examList.value = await getExamList()
    if (examList.value.length) {
      queryParams.examId = examList.value[0].id
    }
  } catch {
    examList.value = []
  }
}

async function loadClasses() {
  try {
    classList.value = await getAllClasses()
  } catch {
    classList.value = []
  }
}

function initCharts() {
  distributionInstance = echarts.init(distributionChart.value)
  subjectInstance = echarts.init(subjectChart.value)

  window.addEventListener('resize', () => {
    distributionInstance?.resize()
    subjectInstance?.resize()
  })
}

watch(() => [queryParams.examId, queryParams.classId], () => {
  if (queryParams.examId) {
    loadStats()
  }
}, { immediate: true })

async function loadStats() {
  if (!queryParams.examId) return

  loading.value = true
  try {
    statsData.value = await getScoreStats(queryParams)
    renderCharts()
  } catch {
    statsData.value = null
  } finally {
    loading.value = false
  }
}

function renderCharts() {
  if (!statsData.value) return

  // 成绩分布饼图
  const distribution = statsData.value.distribution || {
    excellent: 0, good: 0, pass: 0, fail: 0
  }

  distributionInstance?.setOption({
    title: { text: '成绩分布', left: 'center' },
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 10 },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: distribution.excellent, name: '优秀(90-100)', itemStyle: { color: '#10B981' } },
        { value: distribution.good, name: '良好(80-89)', itemStyle: { color: '#3B82F6' } },
        { value: distribution.pass, name: '及格(60-79)', itemStyle: { color: '#F59E0B' } },
        { value: distribution.fail, name: '不及格(<60)', itemStyle: { color: '#EF4444' } }
      ]
    }]
  })

  // 各科平均分柱状图
  const subjects = statsData.value.subjectStats || []

  subjectInstance?.setOption({
    title: { text: '各科平均分', left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: subjects.map(s => s.courseName)
    },
    yAxis: {
      type: 'value',
      max: 100,
      name: '分数'
    },
    series: [{
      type: 'bar',
      data: subjects.map(s => ({
        value: s.avgScore?.toFixed(1) || 0,
        itemStyle: {
          color: s.avgScore >= 80 ? '#10B981' : s.avgScore >= 60 ? '#3B82F6' : '#EF4444'
        }
      })),
      label: {
        show: true,
        position: 'top'
      }
    }]
  })
}
</script>

<template>
  <div class="page-container" v-loading="loading">
    <div class="search-area">
      <el-form inline>
        <el-form-item label="考试">
          <el-select v-model="queryParams.examId" placeholder="选择考试" style="width: 200px">
            <el-option v-for="item in examList" :key="item.id" :label="item.examName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="queryParams.classId" placeholder="全部班级" clearable style="width: 160px">
            <el-option v-for="item in classList" :key="item.id" :label="item.className" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- Stats Cards Grid -->
    <div class="stats-grid">
      <div class="bento-stat-card">
        <div class="stat-icon bg-blue-50 text-blue-500"><el-icon><User /></el-icon></div>
        <div class="stat-content">
          <span class="label">参考人数</span>
          <span class="value">{{ statsData?.studentCount || 0 }}</span>
        </div>
      </div>
      <div class="bento-stat-card">
        <div class="stat-icon bg-emerald-50 text-emerald-500"><el-icon><TrendCharts /></el-icon></div>
        <div class="stat-content">
          <span class="label">平均分</span>
          <span class="value">{{ statsData?.avgScore?.toFixed(1) || '-' }}</span>
        </div>
      </div>
      <div class="bento-stat-card">
        <div class="stat-icon bg-amber-50 text-amber-500"><el-icon><Star /></el-icon></div>
        <div class="stat-content">
          <span class="label">最高分</span>
          <span class="value">{{ statsData?.maxScore || '-' }}</span>
        </div>
      </div>
      <div class="bento-stat-card">
        <div class="stat-icon bg-indigo-50 text-indigo-500"><el-icon><PieChart /></el-icon></div>
        <div class="stat-content">
          <span class="label">及格率</span>
          <span class="value">{{ statsData?.passRate ? (statsData.passRate).toFixed(1) + '%' : '-' }}</span>
        </div>
      </div>
    </div>

    <el-row :gutter="24">
      <el-col :span="12">
        <div class="content-card">
          <div ref="distributionChart" class="chart-container"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="content-card">
          <div ref="subjectChart" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>

    <div class="table-area">
      <div class="toolbar">
        <div class="title">成绩排名 Top 10</div>
      </div>
      <el-table :data="statsData?.topStudents || []" border stripe>
        <el-table-column type="index" label="排名" width="80" align="center" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="totalScore" label="总分" width="100" align="center" />
        <el-table-column prop="avgScore" label="平均分" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <span class="font-bold text-blue-600">{{ row.avgScore?.toFixed(1) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<style scoped lang="scss">
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.bento-stat-card {
  background: #fff;
  border-radius: 24px;
  border: 1px solid #F1F5F9;
  box-shadow: 0 1px 3px rgba(0,0,0,0.02);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;

  &:hover {
    box-shadow: 0 12px 20px -5px rgba(0,0,0,0.05);
  }

  .stat-icon {
    width: 54px; height: 54px; border-radius: 16px;
    display: flex; align-items: center; justify-content: center;
    font-size: 24px;
  }

  .stat-content {
    display: flex;
    flex-direction: column;
    .label { font-size: 13px; font-weight: 600; color: #64748B; margin-bottom: 2px; }
    .value { font-size: 24px; font-weight: 800; color: #0F172A; letter-spacing: -0.5px; }
  }
}

.chart-container {
  height: 350px;
}
</style>
