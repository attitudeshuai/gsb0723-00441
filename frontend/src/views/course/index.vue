<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourseList, addCourse, updateCourse, deleteCourse } from '@/api/course'

const loading = ref(false)
const tableData = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const formData = reactive({
  id: null,
  courseName: '',
  courseCode: '',
  grade: null
})

const rules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  courseCode: [{ required: true, message: '请输入课程代码', trigger: 'blur' }]
}

onMounted(() => {
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    tableData.value = await getCourseList()
  } catch {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  dialogTitle.value = '新增课程'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑课程'
  Object.assign(formData, {
    id: row.id,
    courseName: row.courseName,
    courseCode: row.courseCode,
    grade: row.grade
  })
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除课程"${row.courseName}"吗？`, '提示', { type: 'warning' })
    await deleteCourse(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 取消
  }
}

function resetForm() {
  formData.id = null
  formData.courseName = ''
  formData.courseCode = ''
  formData.grade = null
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    if (formData.id) {
      await updateCourse(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await addCourse(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {
    // 错误已处理
  }
}
</script>

<template>
  <div class="page-container">
    <div class="table-area">
      <div class="toolbar">
        <div class="title">课程管理</div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增课程
        </el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe style="width: 100%">
        <el-table-column prop="courseCode" label="课程代码" min-width="150" />
        <el-table-column prop="courseName" label="课程名称" min-width="200">
          <template #default="{ row }">
            <div class="font-bold text-slate-700">{{ row.courseName }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="适用年级" min-width="150">
          <template #default="{ row }">
            <el-tag :type="row.grade ? 'info' : 'success'" effect="light" round>
              {{ row.grade ? `${row.grade}年级` : '全部年级' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="450px" destroy-on-close append-to-body>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="formData.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程代码" prop="courseCode">
          <el-input v-model="formData.courseCode" placeholder="如YUWEN" :disabled="!!formData.id" />
        </el-form-item>
        <el-form-item label="适用年级" prop="grade">
          <el-select v-model="formData.grade" placeholder="全部年级" clearable style="width: 100%">
            <el-option v-for="i in 6" :key="i" :label="`${i}年级`" :value="i" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
