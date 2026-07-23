<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAttendanceList, inputAttendances } from '@/api/attendance'
import { getAllClasses, getClassStudents } from '@/api/class'
import { formatDate, getAttendanceStatusOptions, formatAttendanceStatus } from '@/utils/format'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const loading = ref(false)
const router = useRouter()
const classList = ref([])
const studentList = ref([])

const queryParams = reactive({
  classId: '',
  attendDate: dayjs().format('YYYY-MM-DD')
})

const attendanceData = ref([])
const saving = ref(false)

onMounted(async () => {
  await loadClasses()
})

async function loadClasses() {
  try {
    classList.value = await getAllClasses()
  } catch {
    classList.value = []
  }
}

async function loadStudents() {
  if (!queryParams.classId) return

  loading.value = true
  try {
    const [students, existingData] = await Promise.all([
      getClassStudents(queryParams.classId),
      getAttendanceList(queryParams).catch(() => [])
    ])

    const existingMap = {}
    existingData.forEach(item => {
      existingMap[item.studentId] = item
    })

    attendanceData.value = students.map(s => ({
      studentId: s.id,
      studentNo: s.studentNo,
      studentName: s.name,
      status: existingMap[s.id]?.status || 1,
      remark: existingMap[s.id]?.remark || ''
    }))
  } catch {
    attendanceData.value = []
  } finally {
    loading.value = false
  }
}

function handleClassChange() {
  loadStudents()
}

function handleDateChange() {
  if (queryParams.classId) {
    loadStudents()
  }
}

async function handleSave() {
  if (!queryParams.classId || !queryParams.attendDate) {
    ElMessage.warning('请选择班级和日期')
    return
  }

  saving.value = true
  try {
    await inputAttendances({
      classId: queryParams.classId,
      attendDate: queryParams.attendDate,
      records: attendanceData.value.map(item => ({
        studentId: item.studentId,
        status: item.status,
        remark: item.remark
      }))
    })
    ElMessage.success('保存成功')
  } catch {
    // 错误已处理
  } finally {
    saving.value = false
  }
}

function setAllStatus(status) {
  attendanceData.value.forEach(item => {
    item.status = status
  })
}
</script>

<template>
  <div class="page-container">
    <div class="search-area">
      <el-form :model="queryParams" inline>
        <el-form-item label="班级">
          <el-select v-model="queryParams.classId" placeholder="选择班级" style="width: 160px" @change="handleClassChange">
            <el-option v-for="item in classList" :key="item.id" :label="item.className" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="queryParams.attendDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">
            <el-icon><Check /></el-icon>保存考勤
          </el-button>
          <el-button type="info" @click="router.push('/attendance/stats')">
            <el-icon><TrendCharts /></el-icon>考勤统计
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-area">
      <template v-if="queryParams.classId">
        <div class="toolbar">
          <div class="title">考勤录入</div>
          <div v-if="attendanceData.length" class="flex items-center gap-3">
            <span class="text-xs text-slate-400 font-semibold uppercase">一键操作</span>
            <el-button size="small" type="success" plain round @click="setAllStatus(1)">全部出勤</el-button>
          </div>
        </div>

        <el-table v-loading="loading" :data="attendanceData" stripe style="width: 100%">
          <el-table-column prop="studentNo" label="学号" min-width="120" />
          <el-table-column prop="studentName" label="姓名" min-width="120">
            <template #default="{ row }">
              <span class="font-bold text-slate-700">{{ row.studentName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="考勤状态" min-width="300">
            <template #default="{ row }">
              <el-radio-group v-model="row.status" size="small" class="modern-radio-group">
                <el-radio-button
                  v-for="option in getAttendanceStatusOptions()"
                  :key="option.value"
                  :value="option.value"
                >
                  {{ option.label }}
                </el-radio-button>
              </el-radio-group>
            </template>
          </el-table-column>
          <el-table-column label="备注" width="250" fixed="right">
            <template #default="{ row }">
              <el-input v-model="row.remark" placeholder="添加备注..." size="small" />
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!attendanceData.length && !loading" description="该班级暂无学生数据" />
      </template>

      <div v-else class="empty-state">
        <div class="empty-state__icon">
          <el-icon><Calendar /></el-icon>
        </div>
        <div class="empty-state__text">请选择班级开始录入考勤</div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.quick-actions {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  color: #666;
}
</style>
