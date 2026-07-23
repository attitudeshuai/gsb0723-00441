<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { getExamList, addExam, updateExam, deleteExam } from '@/api/exam'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新建考试')
const formLoading = ref(false)

// 筛选条件
const filterParams = reactive({
  grade: '',
  examType: '',
  semester: ''
})

// 表单数据
const formData = reactive({
  id: null,
  examName: '',
  examType: null,
  grade: null,
  semester: '',
  examDate: null,
  status: 1
})

// 外层筛选器选项
const filterExamTypeOptions = [
  { label: '期中考试', value: 1 },
  { label: '期末考试', value: 2 },
  { label: '单元测试', value: 3 },
  { label: '月考', value: 4 }
]

// 对话框表单选项（独立副本，防止影响外层筛选器）
const formExamTypeOptions = [
  { label: '期中考试', value: 1 },
  { label: '期末考试', value: 2 },
  { label: '单元测试', value: 3 },
  { label: '月考', value: 4 }
]

const filterGradeOptions = [
  { label: '一年级', value: 1 },
  { label: '二年级', value: 2 },
  { label: '三年级', value: 3 },
  { label: '四年级', value: 4 },
  { label: '五年级', value: 5 },
  { label: '六年级', value: 6 }
]

// 对话框表单选项（独立副本）
const formGradeOptions = [
  { label: '一年级', value: 1 },
  { label: '二年级', value: 2 },
  { label: '三年级', value: 3 },
  { label: '四年级', value: 4 },
  { label: '五年级', value: 5 },
  { label: '六年级', value: 6 }
]

// 对话框表单选项（独立副本）
const formSemesterOptions = computed(() => {
  const currentYear = new Date().getFullYear()
  const options = []
  for (let year = currentYear; year >= currentYear - 2; year--) {
    options.push({ label: `${year}-${year + 1}学年第一学期`, value: `${year}-${year + 1}-1` })
    options.push({ label: `${year}-${year + 1}学年第二学期`, value: `${year}-${year + 1}-2` })
  }
  return options
})

const statusOptions = [
  { label: '进行中', value: 1 },
  { label: '已结束', value: 2 }
]

// 学期选项（动态生成，用于外层筛选器）
const semesterOptions = computed(() => {
  const currentYear = new Date().getFullYear()
  const options = []
  for (let year = currentYear; year >= currentYear - 2; year--) {
    options.push({ label: `${year}-${year + 1}学年第一学期`, value: `${year}-${year + 1}-1` })
    options.push({ label: `${year}-${year + 1}学年第二学期`, value: `${year}-${year + 1}-2` })
  }
  return options
})

// 过滤后的数据
const filteredData = computed(() => {
  return tableData.value.filter(item => {
    if (filterParams.grade && item.grade !== filterParams.grade) return false
    if (filterParams.examType && item.examType !== filterParams.examType) return false
    if (filterParams.semester && item.semester !== filterParams.semester) return false
    return true
  })
})

onMounted(() => {
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    tableData.value = await getExamList()
  } catch {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function getExamTypeName(type) {
  const option = filterExamTypeOptions.find(o => o.value === type)
  return option ? option.label : '-'
}

function getGradeName(grade) {
  const option = filterGradeOptions.find(o => o.value === grade)
  return option ? option.label : '-'
}

function resetForm() {
  formData.id = null
  formData.examName = ''
  formData.examType = null
  formData.grade = null
  formData.semester = ''
  formData.examDate = null
  formData.status = 1
}

function handleAdd() {
  resetForm()
  dialogTitle.value = '新建考试'
  dialogVisible.value = true
}

function handleEdit(row) {
  formData.id = row.id
  formData.examName = row.examName
  formData.examType = row.examType
  formData.grade = row.grade
  formData.semester = row.semester
  formData.examDate = row.examDate
  formData.status = row.status
  dialogTitle.value = '编辑考试'
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除这场考试吗？删除后相关成绩数据也会被删除。', '删除确认', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await deleteExam(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      // 错误已处理
    }
  }
}

async function handleSubmit() {
  if (!formData.examName) {
    ElMessage.warning('请输入考试名称')
    return
  }
  if (!formData.examType) {
    ElMessage.warning('请选择考试类型')
    return
  }

  formLoading.value = true
  try {
    const data = {
      examName: formData.examName,
      examType: formData.examType,
      grade: formData.grade,
      semester: formData.semester,
      examDate: formData.examDate,
      status: formData.status
    }

    if (formData.id) {
      await updateExam(formData.id, data)
      ElMessage.success('更新成功')
    } else {
      await addExam(data)
      ElMessage.success('新建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {
    // 错误已处理
  } finally {
    formLoading.value = false
  }
}

function handleReset() {
  filterParams.grade = ''
  filterParams.examType = ''
  filterParams.semester = ''
}

function handleCancel() {
  dialogVisible.value = false
}

function handleDialogClosed() {
  // 在对话框完全关闭后重置表单
  resetForm()
}
</script>

<template>
  <div class="page-container">
    <div class="search-area">
      <el-form :model="filterParams" inline>
        <el-form-item label="年级">
          <el-select v-model="filterParams.grade" placeholder="全部年级" clearable style="width: 120px">
            <el-option v-for="item in filterGradeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试类型">
          <el-select v-model="filterParams.examType" placeholder="全部类型" clearable style="width: 120px">
            <el-option v-for="item in filterExamTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="学期">
          <el-select v-model="filterParams.semester" placeholder="全部学期" clearable style="width: 200px">
            <el-option v-for="item in semesterOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新建考试
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-area">
      <div class="toolbar">
        <div class="title">考试列表</div>
        <div class="count">共 {{ filteredData.length }} 条记录</div>
      </div>
      <el-table v-loading="loading" :data="filteredData" stripe>
        <el-table-column prop="examName" label="考试名称" min-width="280" />
        <el-table-column label="考试类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.examType === 1 ? 'warning' : row.examType === 2 ? 'danger' : row.examType === 3 ? 'info' : 'primary'"
              effect="light"
            >
              {{ getExamTypeName(row.examType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="年级" width="100" align="center">
          <template #default="{ row }">
            {{ getGradeName(row.grade) }}
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" width="140" align="center" />
        <el-table-column prop="examDate" label="考试日期" width="120" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="light">
              {{ row.status === 1 ? '进行中' : '已结束' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="550px"
      :close-on-click-modal="true"
      :close-on-press-escape="true"
      :destroy-on-close="true"
      @closed="handleDialogClosed"
    >
      <el-form :model="formData" label-width="80px">
        <el-form-item label="考试名称" required>
          <el-input v-model="formData.examName" placeholder="请输入考试名称" />
        </el-form-item>
        <el-form-item label="考试类型" required>
          <el-select v-model="formData.examType" placeholder="请选择考试类型" style="width: 100%" :teleported="false" popper-class="dialog-exam-type-select">
            <el-option v-for="item in formExamTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="年级">
          <el-select v-model="formData.grade" placeholder="请选择年级" clearable style="width: 100%" :teleported="false">
            <el-option v-for="item in formGradeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="学期">
          <el-select v-model="formData.semester" placeholder="请选择学期" clearable style="width: 100%" :teleported="false">
            <el-option v-for="item in formSemesterOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试日期">
          <el-date-picker
            v-model="formData.examDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio v-for="item in statusOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  .title {
    font-size: 16px;
    font-weight: 500;
  }

  .count {
    color: #909399;
    font-size: 14px;
  }
}
</style>
