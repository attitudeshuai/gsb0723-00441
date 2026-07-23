<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { getStudentList, addStudent, updateStudent, deleteStudent, transferStudent } from '@/api/student'
import { getAllClasses } from '@/api/class'
import { formatDate, formatGender, formatStudentStatus, getGenderOptions, getStudentStatusOptions } from '@/utils/format'

const loading = ref(false)
const router = useRouter()
const tableData = ref([])
const total = ref(0)
const classList = ref([])

const queryParams = reactive({
  page: 1,
  size: 10,
  classId: '',
  name: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const formData = reactive({
  id: null,
  name: '',
  gender: 1,
  birthDate: '',
  classId: '',
  parentName: '',
  parentPhone: '',
  address: '',
  enrollDate: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  parentPhone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

onMounted(async () => {
  await loadClasses()
  await loadData()
})

async function loadClasses() {
  try {
    classList.value = await getAllClasses()
  } catch {
    classList.value = []
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getStudentList(queryParams)
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
  queryParams.classId = ''
  queryParams.name = ''
  queryParams.status = ''
  handleSearch()
}

function handleAdd() {
  dialogTitle.value = '新增学生'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑学生'
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    gender: row.gender,
    birthDate: row.birthDate,
    classId: row.classId,
    parentName: row.parentName,
    parentPhone: row.parentPhone,
    address: row.address,
    enrollDate: row.enrollDate
  })
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除学生"${row.name}"吗？`, '提示', {
      type: 'warning'
    })
    await deleteStudent(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 取消删除
  }
}

function resetForm() {
  formData.id = null
  formData.name = ''
  formData.gender = 1
  formData.birthDate = ''
  formData.classId = ''
  formData.parentName = ''
  formData.parentPhone = ''
  formData.address = ''
  formData.enrollDate = ''
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    if (formData.id) {
      await updateStudent(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await addStudent(formData)
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

// 转班功能
const transferVisible = ref(false)
const transferForm = reactive({
  studentId: null,
  studentName: '',
  currentClass: '',
  targetClassId: ''
})

function handleTransfer(row) {
  transferForm.studentId = row.id
  transferForm.studentName = row.name
  transferForm.currentClass = row.className
  transferForm.targetClassId = ''
  transferVisible.value = true
}

async function submitTransfer() {
  if (!transferForm.targetClassId) {
    ElMessage.warning('请选择目标班级')
    return
  }
  try {
    await transferStudent(transferForm.studentId, transferForm.targetClassId)
    ElMessage.success('转班成功')
    transferVisible.value = false
    loadData()
  } catch {
    // 错误已处理
  }
}

</script>

<template>
  <div class="page-container">
    <div class="search-area">
      <el-form :model="queryParams" inline>
        <el-form-item label="班级">
          <el-select v-model="queryParams.classId" placeholder="全部" clearable style="width: 160px">
            <el-option
              v-for="item in classList"
              :key="item.id"
              :label="item.className"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option
              v-for="item in getStudentStatusOptions()"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-area">
      <div class="toolbar">
        <div class="title">学生列表</div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增学生
        </el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe style="width: 100%">
        <el-table-column prop="studentNo" label="学号" min-width="120" />
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="gender" label="性别" min-width="80" align="center">
          <template #default="{ row }">
            <span :class="row.gender === 1 ? 'text-blue-500' : 'text-pink-500'">
              {{ formatGender(row.gender) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="className" label="班级" min-width="120" />
        <el-table-column prop="parentPhone" label="家长电话" min-width="130" />
        <el-table-column prop="status" label="状态" min-width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : 'info'" effect="light" round>
              {{ formatStudentStatus(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="router.push(`/student/${row.id}`)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleTransfer(row)">转班</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close append-to-body>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="formData.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="formData.gender">
                <el-radio v-for="item in getGenderOptions()" :key="item.value" :value="item.value">
                  {{ item.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="formData.birthDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级" prop="classId">
              <el-select v-model="formData.classId" placeholder="请选择班级" style="width: 100%">
                <el-option
                  v-for="item in classList"
                  :key="item.id"
                  :label="item.className"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="家长姓名" prop="parentName">
              <el-input v-model="formData.parentName" placeholder="请输入家长姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="家长电话" prop="parentPhone">
              <el-input v-model="formData.parentPhone" placeholder="请输入家长电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入学日期" prop="enrollDate">
              <el-date-picker
                v-model="formData.enrollDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="家庭住址" prop="address">
              <el-input v-model="formData.address" placeholder="请输入家庭住址" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 转班对话框 -->
    <el-dialog v-model="transferVisible" title="学生转班" width="450px" destroy-on-close append-to-body>
      <el-form label-width="100px">
        <el-form-item label="学生姓名">
          <el-input :value="transferForm.studentName" disabled />
        </el-form-item>
        <el-form-item label="当前班级">
          <el-input :value="transferForm.currentClass" disabled />
        </el-form-item>
        <el-form-item label="目标班级" required>
          <el-select v-model="transferForm.targetClassId" placeholder="请选择目标班级" style="width: 100%">
            <el-option
              v-for="item in classList"
              :key="item.id"
              :label="item.className"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTransfer">确定转班</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
</style>
