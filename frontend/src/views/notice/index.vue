<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNoticeList, addNotice, updateNotice, deleteNotice, toggleNoticeTop } from '@/api/notice'
import { formatDateTime, formatNoticeType, getNoticeTypeOptions } from '@/utils/format'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 10,
  type: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const formData = reactive({
  id: null,
  title: '',
  content: '',
  type: 1,
  isTop: false
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

onMounted(() => {
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const res = await getNoticeList(queryParams)
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

function handleAdd() {
  dialogTitle.value = '发布公告'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑公告'
  Object.assign(formData, {
    id: row.id,
    title: row.title,
    content: row.content,
    type: row.type,
    isTop: row.isTop
  })
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除公告"${row.title}"吗？`, '提示', { type: 'warning' })
    await deleteNotice(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 取消
  }
}

async function handleToggleTop(row) {
  try {
    await toggleNoticeTop(row.id)
    ElMessage.success(row.isTop ? '取消置顶成功' : '置顶成功')
    loadData()
  } catch {
    // 错误已处理
  }
}

function resetForm() {
  formData.id = null
  formData.title = ''
  formData.content = ''
  formData.type = 1
  formData.isTop = false
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    if (formData.id) {
      await updateNotice(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await addNotice(formData)
      ElMessage.success('发布成功')
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
        <el-form-item label="类型">
          <el-select v-model="queryParams.type" placeholder="全部" clearable style="width: 140px" @change="handleSearch">
            <el-option v-for="item in getNoticeTypeOptions()" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>发布公告
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-area">
      <div class="toolbar">
        <div class="title">公告列表</div>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe style="width: 100%">
        <el-table-column label="标题" min-width="250">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <el-tag v-if="row.isTop" type="danger" size="small" effect="dark" round>置顶</el-tag>
              <span class="font-semibold text-slate-700">{{ row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" min-width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === 3 ? 'danger' : row.type === 2 ? 'warning' : 'info'" effect="light" round>
              {{ formatNoticeType(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布者" min-width="120" />
        <el-table-column prop="publishTime" label="发布时间" min-width="180">
          <template #default="{ row }">
            <span class="text-slate-500 text-xs">{{ formatDateTime(row.publishTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleToggleTop(row)">
              {{ row.isTop ? '取消置顶' : '置顶' }}
            </el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" destroy-on-close append-to-body>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="formData.type">
            <el-radio v-for="item in getNoticeTypeOptions()" :key="item.value" :value="item.value">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="8" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch v-model="formData.isTop" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.top-tag {
  margin-right: 8px;
}
</style>
