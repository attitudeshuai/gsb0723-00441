<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, addRole, updateRole, deleteRole } from '@/api/role'

const loading = ref(false)
const tableData = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const formData = reactive({
  id: null,
  roleName: '',
  roleCode: '',
  permissions: [],
  remark: ''
})

const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const permissionOptions = [
  { label: '首页', value: 'dashboard' },
  { label: '学生管理', value: 'student:*', children: [
    { label: '查看', value: 'student:list' },
    { label: '新增', value: 'student:add' },
    { label: '编辑', value: 'student:edit' },
    { label: '删除', value: 'student:delete' }
  ]},
  { label: '教师管理', value: 'teacher:*', children: [
    { label: '查看', value: 'teacher:list' },
    { label: '新增', value: 'teacher:add' },
    { label: '编辑', value: 'teacher:edit' },
    { label: '删除', value: 'teacher:delete' }
  ]},
  { label: '班级管理', value: 'class:*' },
  { label: '成绩管理', value: 'score:*', children: [
    { label: '查看', value: 'score:list' },
    { label: '录入', value: 'score:input' }
  ]},
  { label: '考勤管理', value: 'attendance:*', children: [
    { label: '查看', value: 'attendance:list' },
    { label: '录入', value: 'attendance:input' }
  ]},
  { label: '通知公告', value: 'notice:*' },
  { label: '用户管理', value: 'user:*' },
  { label: '角色管理', value: 'role:*' }
]

onMounted(() => {
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    tableData.value = await getRoleList()
  } catch {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  dialogTitle.value = '新增角色'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑角色'
  Object.assign(formData, {
    id: row.id,
    roleName: row.roleName,
    roleCode: row.roleCode,
    permissions: row.permissions ? JSON.parse(row.permissions) : [],
    remark: row.remark
  })
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除角色"${row.roleName}"吗？`, '提示', { type: 'warning' })
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 取消
  }
}

function resetForm() {
  formData.id = null
  formData.roleName = ''
  formData.roleCode = ''
  formData.permissions = []
  formData.remark = ''
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    const submitData = {
      ...formData,
      permissions: JSON.stringify(formData.permissions)
    }

    if (formData.id) {
      await updateRole(formData.id, submitData)
      ElMessage.success('更新成功')
    } else {
      await addRole(submitData)
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
        <div class="title">角色管理</div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增角色
        </el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe style="width: 100%">
        <el-table-column prop="roleName" label="角色名称" min-width="150" />
        <el-table-column prop="roleCode" label="角色编码" min-width="150" />
        <el-table-column prop="remark" label="备注" min-width="250" />
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button
              type="danger"
              link
              :disabled="row.roleCode === 'admin'"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close append-to-body>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" placeholder="如admin" :disabled="!!formData.id" />
        </el-form-item>
        <el-form-item label="权限配置">
          <el-checkbox-group v-model="formData.permissions">
            <template v-for="item in permissionOptions" :key="item.value">
              <div class="permission-item">
                <el-checkbox :value="item.value">{{ item.label }}</el-checkbox>
                <div v-if="item.children" class="permission-children">
                  <el-checkbox
                    v-for="child in item.children"
                    :key="child.value"
                    :value="child.value"
                  >
                    {{ child.label }}
                  </el-checkbox>
                </div>
              </div>
            </template>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.permission-item {
  margin-bottom: 12px;
  width: 100%;

  .permission-children {
    margin-left: 24px;
    margin-top: 8px;
    display: flex;
    gap: 16px;
    flex-wrap: wrap;
  }
}
</style>
