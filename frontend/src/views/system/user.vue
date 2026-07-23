<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, addUser, updateUser, deleteUser, resetPassword, toggleUserStatus } from '@/api/user'
import { getAllRoles } from '@/api/role'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const roleList = ref([])

const queryParams = reactive({
  page: 1,
  size: 10,
  username: '',
  realName: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const formData = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  phone: '',
  roleId: '',
  status: 1
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度需在4-20个字符之间', trigger: 'blur' },
    { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '用户名需字母开头，只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度需在6-20个字符之间', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { max: 20, message: '姓名长度不能超过20个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

onMounted(async () => {
  await loadRoles()
  await loadData()
})

async function loadRoles() {
  try {
    roleList.value = await getAllRoles()
  } catch {
    roleList.value = []
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getUserList(queryParams)
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
  queryParams.username = ''
  queryParams.realName = ''
  handleSearch()
}

function handleAdd() {
  dialogTitle.value = '新增用户'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑用户'
  Object.assign(formData, {
    id: row.id,
    username: row.username,
    password: undefined, // 编辑时不发送密码字段
    realName: row.realName,
    phone: row.phone || '',
    roleId: row.roleId,
    status: row.status
  })
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除用户"${row.username}"吗？`, '提示', { type: 'warning' })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 取消
  }
}

async function handleResetPassword(row) {
  try {
    await ElMessageBox.confirm(`确定要重置用户"${row.username}"的密码吗？`, '提示', { type: 'warning' })
    await resetPassword(row.id)
    ElMessage.success('密码已重置为123456')
  } catch {
    // 取消
  }
}

async function handleToggleStatus(row) {
  try {
    await toggleUserStatus(row.id)
    ElMessage.success(row.status === 1 ? '已禁用' : '已启用')
    loadData()
  } catch {
    // 错误已处理
  }
}

function resetForm() {
  formData.id = null
  formData.username = ''
  formData.password = ''
  formData.realName = ''
  formData.phone = ''
  formData.roleId = undefined
  formData.status = 1
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    // 构建提交数据，过滤掉undefined值
    const submitData = {}
    if (formData.id) submitData.id = formData.id
    submitData.username = formData.username
    submitData.realName = formData.realName
    if (formData.phone) submitData.phone = formData.phone
    if (formData.roleId !== undefined && formData.roleId !== null) {
      submitData.roleId = formData.roleId
    }
    submitData.status = formData.status
    // 新增时才需要密码
    if (!formData.id && formData.password) {
      submitData.password = formData.password
    }

    if (formData.id) {
      await updateUser(formData.id, submitData)
      ElMessage.success('更新成功')
    } else {
      await addUser(submitData)
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
</script>

<template>
  <div class="page-container">
    <div class="search-area">
      <el-form :model="queryParams" inline>
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.realName" placeholder="请输入姓名" clearable style="width: 160px" />
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
        <div class="title">用户管理</div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增用户
        </el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe style="width: 100%">
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="姓名" min-width="120">
          <template #default="{ row }">
            <span class="font-bold text-slate-700">{{ row.realName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="roleName" label="角色" min-width="120">
          <template #default="{ row }">
            <el-tag effect="light" round size="small">{{ row.roleName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              :disabled="row.username === 'admin'"
              @change="handleToggleStatus(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleResetPassword(row)">重置密码</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          :total="total"
          :page-size="queryParams.size"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close append-to-body>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" :disabled="!!formData.id" />
        </el-form-item>
        <el-form-item v-if="!formData.id" label="密码" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="formData.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option v-for="item in roleList" :key="item.id" :label="item.roleName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
