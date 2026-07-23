<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getScoreList, inputScores } from '@/api/score'
import { getExamList } from '@/api/exam'
import { getAllClasses } from '@/api/class'
import { getAllCourses } from '@/api/course'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const router = useRouter()
const tableData = ref([])
const examList = ref([])
const classList = ref([])
const courseList = ref([])

const queryParams = reactive({
  examId: '',
  classId: '',
  courseId: ''
})

const inputMode = ref(false)
const inputLoading = ref(false)
const inputData = ref([])

// 计算选中的考试信息
const selectedExam = computed(() => {
  if (!queryParams.examId) return null
  return examList.value.find(e => e.id === queryParams.examId)
})

// 根据考试年级过滤班级
const filteredClasses = computed(() => {
  if (!selectedExam.value || !selectedExam.value.grade) {
    return classList.value
  }
  return classList.value.filter(c => c.grade === selectedExam.value.grade)
})

onMounted(async () => {
  await Promise.all([loadExams(), loadClasses(), loadCourses()])
})

async function loadExams() {
  try {
    examList.value = await getExamList()
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

async function loadCourses() {
  try {
    courseList.value = await getAllCourses()
  } catch {
    courseList.value = []
  }
}

// 当考试变化时，重置班级选择
function onExamChange() {
  queryParams.classId = ''
  tableData.value = []
}

async function loadData() {
  if (!queryParams.examId || !queryParams.classId || !queryParams.courseId) {
    ElMessage.warning('请选择考试、班级和科目')
    return
  }

  loading.value = true
  try {
    const res = await getScoreList(queryParams)
    tableData.value = res || []
  } catch {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function handleInput() {
  if (!queryParams.examId || !queryParams.classId || !queryParams.courseId) {
    ElMessage.warning('请先选择考试、班级和科目')
    return
  }
  loadDataForInput()
}

async function loadDataForInput() {
  loading.value = true
  try {
    const res = await getScoreList(queryParams)
    tableData.value = res || []
    if (tableData.value.length === 0) {
      ElMessage.warning('该班级暂无学生')
      return
    }
    inputMode.value = true
    inputData.value = tableData.value.map(item => ({
      studentId: item.studentId,
      studentNo: item.studentNo,
      studentName: item.studentName,
      score: item.score ?? ''
    }))
  } catch {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

async function handleSaveScores() {
  const validScores = inputData.value.filter(item => item.score !== '' && item.score !== null && item.score !== undefined)

  if (validScores.length === 0) {
    ElMessage.warning('请至少录入一条成绩')
    return
  }

  inputLoading.value = true
  try {
    const scores = validScores.map(item => ({
      studentId: item.studentId,
      score: Number(item.score)
    }))

    await inputScores({
      examId: queryParams.examId,
      courseId: queryParams.courseId,
      scores
    })

    ElMessage.success('保存成功')
    inputMode.value = false
    loadData()
  } catch {
    // 错误已处理
  } finally {
    inputLoading.value = false
  }
}

function cancelInput() {
  inputMode.value = false
}

// 计算统计信息
const stats = computed(() => {
  const scores = tableData.value.filter(item => item.score !== null && item.score !== undefined)
  if (scores.length === 0) return null

  const scoreValues = scores.map(s => Number(s.score))
  const avg = scoreValues.reduce((a, b) => a + b, 0) / scoreValues.length
  const max = Math.max(...scoreValues)
  const min = Math.min(...scoreValues)
  const passCount = scoreValues.filter(s => s >= 60).length
  const excellentCount = scoreValues.filter(s => s >= 90).length

  return {
    count: scores.length,
    avg: avg.toFixed(1),
    max,
    min,
    passRate: ((passCount / scores.length) * 100).toFixed(1),
    excellentRate: ((excellentCount / scores.length) * 100).toFixed(1)
  }
})
</script>

<template>
  <div class="page-container">
    <div class="search-area">
      <el-form :model="queryParams" inline>
        <el-form-item label="考试">
          <el-select
            v-model="queryParams.examId"
            placeholder="选择考试"
            style="width: 280px"
            filterable
            @change="onExamChange"
          >
            <el-option v-for="item in examList" :key="item.id" :label="item.examName" :value="item.id" />
          </el-select>
          <el-button type="primary" link @click="router.push('/exam')" style="margin-left: 8px">
            <el-icon><Setting /></el-icon>管理考试
          </el-button>
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="queryParams.classId" placeholder="选择班级" style="width: 140px">
            <el-option v-for="item in filteredClasses" :key="item.id" :label="item.className" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="科目">
          <el-select v-model="queryParams.courseId" placeholder="选择科目" style="width: 120px">
            <el-option v-for="item in courseList" :key="item.id" :label="item.courseName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button v-if="!inputMode" type="success" @click="handleInput">
            <el-icon><Edit /></el-icon>录入成绩
          </el-button>
          <el-button type="info" @click="router.push('/score/stats')">
            <el-icon><TrendCharts /></el-icon>成绩统计
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 统计信息卡片 -->
    <div v-if="stats && !inputMode" class="stats-cards">
      <div class="stat-card">
        <div class="stat-value">{{ stats.count }}</div>
        <div class="stat-label">已录入人数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.avg }}</div>
        <div class="stat-label">平均分</div>
      </div>
      <div class="stat-card">
        <div class="stat-value text-green">{{ stats.max }}</div>
        <div class="stat-label">最高分</div>
      </div>
      <div class="stat-card">
        <div class="stat-value text-red">{{ stats.min }}</div>
        <div class="stat-label">最低分</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.passRate }}%</div>
        <div class="stat-label">及格率</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.excellentRate }}%</div>
        <div class="stat-label">优秀率</div>
      </div>
    </div>

    <div class="table-area">
      <template v-if="inputMode">
        <div class="toolbar">
          <div class="title flex items-center gap-2">
            成绩录入
            <el-tag type="warning" effect="light" round size="small">录入模式</el-tag>
          </div>
          <div>
            <el-button @click="cancelInput">取消</el-button>
            <el-button type="primary" :loading="inputLoading" @click="handleSaveScores">保存成绩</el-button>
          </div>
        </div>
        <el-table :data="inputData" border>
          <el-table-column prop="studentNo" label="学号" width="120" />
          <el-table-column prop="studentName" label="姓名" width="100" />
          <el-table-column label="分数" width="200">
            <template #default="{ row }">
              <el-input-number v-model="row.score" :min="0" :max="100" :precision="1" size="small" style="width: 100%" />
            </template>
          </el-table-column>
        </el-table>
      </template>

      <template v-else-if="tableData.length || loading">
        <div class="toolbar">
          <div class="title">成绩列表</div>
        </div>
        <el-table v-loading="loading" :data="tableData" stripe style="width: 100%">
          <el-table-column prop="studentNo" label="学号" min-width="120" />
          <el-table-column prop="studentName" label="姓名" min-width="120" />
          <el-table-column prop="score" label="分数" min-width="120" align="center">
            <template #default="{ row }">
              <span v-if="row.score !== null" class="font-bold text-lg" :class="row.score >= 60 ? 'text-blue-500' : 'text-red-500'">
                {{ row.score }}
              </span>
              <span v-else class="text-slate-400">-</span>
            </template>
          </el-table-column>
          <el-table-column label="等第" width="120" align="center" fixed="right">
            <template #default="{ row }">
              <el-tag v-if="row.score !== null" :type="row.score >= 90 ? 'success' : row.score >= 80 ? 'info' : row.score >= 60 ? 'warning' : 'danger'" effect="light" round>
                {{ row.score >= 90 ? '优秀' : row.score >= 80 ? '良好' : row.score >= 60 ? '及格' : '不及格' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </template>

      <div v-else class="empty-state">
        <div class="empty-state__icon">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="empty-state__text">请选择查询条件后点击查询</div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.stats-cards {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  flex-wrap: wrap;

  .stat-card {
    flex: 1;
    min-width: 120px;
    background: #fff;
    border-radius: 8px;
    padding: 16px;
    text-align: center;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

    .stat-value {
      font-size: 24px;
      font-weight: 600;
      color: #303133;

      &.text-green {
        color: #67c23a;
      }

      &.text-red {
        color: #f56c6c;
      }
    }

    .stat-label {
      font-size: 12px;
      color: #909399;
      margin-top: 4px;
    }
  }
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  .title {
    font-size: 16px;
    font-weight: 500;
  }
}
</style>
