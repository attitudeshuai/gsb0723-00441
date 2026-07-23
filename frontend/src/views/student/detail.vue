<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getStudent } from '@/api/student'
import { getScoreList } from '@/api/score'
import { getAttendanceList } from '@/api/attendance'
import { formatDate, formatGender, formatStudentStatus, formatAttendanceStatus } from '@/utils/format'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const student = ref({})
const scores = ref([])
const attendances = ref([])
const activeTab = ref('info')

onMounted(() => {
  loadStudentDetail()
})

async function loadStudentDetail() {
  const id = route.params.id
  if (!id) {
    router.push('/student')
    return
  }

  loading.value = true
  try {
    const [studentData, scoreData, attendanceData] = await Promise.all([
      getStudent(id),
      getScoreList({ studentId: id, size: 50 }).catch(() => ({ records: [] })),
      getAttendanceList({ studentId: id, size: 50 }).catch(() => [])
    ])

    student.value = studentData || {}
    scores.value = scoreData?.records || []
    attendances.value = Array.isArray(attendanceData) ? attendanceData : []
  } catch {
    student.value = {}
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.push('/student')
}
</script>

<template>
  <div class="page-container" v-loading="loading">
    <div class="page-header">
      <el-button @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h2>学生详情</h2>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="基本信息" name="info">
        <el-card>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="学号">{{ student.studentNo }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ student.name }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ formatGender(student.gender) }}</el-descriptions-item>
            <el-descriptions-item label="出生日期">{{ formatDate(student.birthDate) }}</el-descriptions-item>
            <el-descriptions-item label="班级">{{ student.className }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="student.status === 1 ? 'success' : student.status === 2 ? 'warning' : 'info'" size="small">
                {{ formatStudentStatus(student.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="入学日期">{{ formatDate(student.enrollDate) }}</el-descriptions-item>
            <el-descriptions-item label="家长姓名">{{ student.parentName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="家长电话">{{ student.parentPhone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="家庭住址" :span="3">{{ student.address || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="成绩记录" name="scores">
        <el-card>
          <el-table :data="scores" border stripe>
            <el-table-column prop="examName" label="考试名称" min-width="150" />
            <el-table-column prop="courseName" label="科目" width="100" />
            <el-table-column prop="score" label="成绩" width="80" align="center">
              <template #default="{ row }">
                <span :class="{ 'score-fail': row.score < 60 }">{{ row.score }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="rank" label="班级排名" width="100" align="center" />
            <el-table-column prop="examDate" label="考试日期" width="120">
              <template #default="{ row }">{{ formatDate(row.examDate) }}</template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!scores.length" description="暂无成绩记录" />
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="考勤记录" name="attendance">
        <el-card>
          <el-table :data="attendances" border stripe>
            <el-table-column prop="attendDate" label="日期" width="120">
              <template #default="{ row }">{{ formatDate(row.attendDate) }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag
                  :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : row.status === 3 ? 'warning' : 'info'"
                  size="small"
                >
                  {{ formatAttendanceStatus(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="200" />
          </el-table>
          <el-empty v-if="!attendances.length" description="暂无考勤记录" />
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped lang="scss">
.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;

  h2 {
    margin: 0;
    font-size: 18px;
  }
}

.score-fail {
  color: #f56c6c;
  font-weight: bold;
}

.el-card {
  margin-top: 16px;
}
</style>
