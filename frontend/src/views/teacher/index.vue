<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTeacherList, addTeacher, updateTeacher, deleteTeacher } from '@/api/teacher'
import { formatGender, getGenderOptions } from '@/utils/format'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 10,
  name: '',
  subject: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const formData = reactive({
  id: null,
  name: '',
  gender: 1,
  phone: '',
  subject: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
  subject: [{ required: true, message: '请输入任教科目', trigger: 'blur' }]
}

const subjectOptions = ['语文', '数学', '英语', '科学', '道德与法治', '音乐', '美术', '体育', '信息技术']

onMounted(() => {
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const res = await getTeacherList(queryParams)
    tableData.value = res?.records || []
    total.value = res?.total || 0
  } catch {
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  loadData()
}

function handleReset() {
  queryParams.name = ''
  queryParams.subject = ''
  handleSearch()
}

function handleAdd() {
  dialogTitle.value = '新增教师'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑教师'
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    gender: row.gender,
    phone: row.phone,
    subject: row.subject
  })
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除教师"${row.name}"吗？`, '提示', { type: 'warning' })
    await deleteTeacher(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 取消
  }
}

function resetForm() {
  formData.id = null
  formData.name = ''
  formData.gender = 1
  formData.phone = ''
  formData.subject = ''
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    if (formData.id) {
      await updateTeacher(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await addTeacher(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {
    // 错误已处理
  }
}

function handlePageChange(page) {
  queryParams.page = page
  loadData()
}

function handleSizeChange(size) {
  queryParams.size = size
  queryParams.page = 1
  loadData()
}
</script>

<template>
  <div class="page-container">
    <div class="search-area">
      <el-form :model="queryParams" inline>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="任教科目">
          <el-select v-model="queryParams.subject" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="item in subjectOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-area">
      <div class="toolbar">
        <div class="title">教师列表</div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增教师
        </el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe style="width: 100%">
        <el-table-column prop="teacherNo" label="工号" min-width="120" />
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="gender" label="性别" min-width="80" align="center">
          <template #default="{ row }">
            <span :class="row.gender === 1 ? 'text-blue-500' : 'text-pink-500'">
              {{ formatGender(row.gender) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="subject" label="任教科目" min-width="120" />
        <el-table-column prop="headTeacherClass" label="班主任" min-width="120">
          <template #default="{ row }">
            <el-tag v-if="row.isHeadTeacher" type="success" size="small" effect="light" round>{{ row.headTeacherClass }}</el-tag>
            <span v-else class="text-gray-400">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="light" round>
              {{ row.status === 1 ? '在职' : '离职' }}
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

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close append-to-body>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio v-for="item in getGenderOptions()" :key="item.value" :value="item.value">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="任教科目" prop="subject">
          <el-select v-model="formData.subject" placeholder="请选择科目" style="width: 100%">
            <el-option v-for="item in subjectOptions" :key="item" :label="item" :value="item" />
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
