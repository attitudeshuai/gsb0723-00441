<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getLogList, exportLogs } from '@/api/log'

const loading = ref(false)
const logList = ref([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  pageSize: 20,
  username: '',
  module: '',
  operation: '',
  startDate: '',
  endDate: ''
})

const dateRange = ref([])

// 模块选项
const moduleOptions = [
  { label: '全部', value: '' },
  { label: '用户管理', value: 'user' },
  { label: '角色管理', value: 'role' },
  { label: '学生管理', value: 'student' },
  { label: '教师管理', value: 'teacher' },
  { label: '班级管理', value: 'class' },
  { label: '课程管理', value: 'course' },
  { label: '成绩管理', value: 'score' },
  { label: '考勤管理', value: 'attendance' },
  { label: '通知公告', value: 'notice' },
  { label: '系统登录', value: 'auth' }
]

// 操作类型选项 - 根据operation字段中的关键词来筛选
const actionOptions = [
  { label: '全部', value: '' },
  { label: '新增', value: '新增' },
  { label: '修改', value: '修改' },
  { label: '删除', value: '删除' },
  { label: '查询', value: '查询' },
  { label: '导入', value: '导入' },
  { label: '导出', value: '导出' },
  { label: '登录', value: '登录' },
  { label: '登出', value: '登出' }
]


// 模块中文
const moduleText = {
  user: '用户管理',
  role: '角色管理',
  student: '学生管理',
  teacher: '教师管理',
  class: '班级管理',
  course: '课程管理',
  score: '成绩管理',
  attendance: '考勤管理',
  notice: '通知公告',
  auth: '系统登录'
}

onMounted(() => {
  fetchLogList()
})

async function fetchLogList() {
  loading.value = true
  try {
    // 处理日期范围
    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.startDate = dateRange.value[0]
      queryParams.endDate = dateRange.value[1]
    } else {
      queryParams.startDate = ''
      queryParams.endDate = ''
    }

    const res = await getLogList(queryParams)
    logList.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('获取日志列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  fetchLogList()
}

function handleReset() {
  queryParams.username = ''
  queryParams.module = ''
  queryParams.operation = ''
  dateRange.value = []
  queryParams.startDate = ''
  queryParams.endDate = ''
  queryParams.page = 1
  fetchLogList()
}

function handlePageChange(page) {
  queryParams.page = page
  fetchLogList()
}

function handleSizeChange(size) {
  queryParams.pageSize = size
  queryParams.page = 1
  fetchLogList()
}

async function handleExport() {
  try {
    const res = await exportLogs(queryParams)
    const blob = new Blob([res], { type: 'text/csv;charset=utf-8' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `操作日志_${new Date().toISOString().slice(0, 10)}.csv`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出日志失败:', error)
    ElMessage.error('导出失败')
  }
}

// 详情对话框
const detailVisible = ref(false)
const currentLog = ref({})

function handleViewDetail(row) {
  currentLog.value = row
  detailVisible.value = true
}

// 根据操作描述获取标签颜色
function getOperationTagType(operation) {
  if (!operation) return 'info'
  if (operation.includes('新增')) return 'success'
  if (operation.includes('修改')) return 'warning'
  if (operation.includes('删除')) return 'danger'
  if (operation.includes('导入')) return 'primary'
  if (operation.includes('导出')) return 'primary'
  if (operation.includes('登录')) return 'success'
  if (operation.includes('登出')) return 'info'
  return 'info'
}
</script>

<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :model="queryParams" inline>
        <el-form-item label="操作人">
          <el-input
            v-model="queryParams.username"
            placeholder="请输入用户名"
            clearable
            style="width: 160px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="操作模块">
          <el-select
            v-model="queryParams.module"
            placeholder="请选择"
            clearable
            style="width: 140px"
          >
            <el-option
              v-for="item in moduleOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select
            v-model="queryParams.operation"
            placeholder="请选择"
            clearable
            style="width: 120px"
          >
            <el-option
              v-for="item in actionOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="操作时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px"
          />
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

    <!-- 日志列表 -->
    <div class="table-area">
      <div class="toolbar">
        <div class="title">操作日志</div>
        <div class="flex gap-2">
          <el-button type="primary" plain round size="small" @click="handleExport">
            <el-icon><Download /></el-icon>导出
          </el-button>
        </div>
      </div>

      <el-table v-loading="loading" :data="logList" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="操作人" width="120" align="center" />
        <el-table-column prop="module" label="操作模块" width="120" align="center">
          <template #default="{ row }">
            {{ moduleText[row.module] || row.module }}
          </template>
        </el-table-column>
        <el-table-column prop="operation" label="操作类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getOperationTagType(row.operation)" effect="light" round size="small">
              {{ row.operation || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operation" label="操作描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP地址" width="140" align="center" />
        <el-table-column prop="costTime" label="耗时(ms)" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.costTime > 1000 ? 'danger' : row.costTime > 500 ? 'warning' : 'success'"
              size="small"
              effect="light"
            >
              {{ row.costTime }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="light" round size="small">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="180" align="center" />
        <el-table-column label="操作" width="80" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleViewDetail(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="日志详情" width="600px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentLog.username }}</el-descriptions-item>
        <el-descriptions-item label="操作模块">
          {{ moduleText[currentLog.module] || currentLog.module }}
        </el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="getOperationTagType(currentLog.operation)" size="small">
            {{ currentLog.operation || '-' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ip }}</el-descriptions-item>
        <el-descriptions-item label="耗时">{{ currentLog.costTime }}ms</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentLog.status === 1 ? 'success' : 'danger'" size="small">
            {{ currentLog.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ currentLog.createTime }}</el-descriptions-item>
        <el-descriptions-item label="操作描述" :span="2">
          {{ currentLog.operation }}
        </el-descriptions-item>
        <el-descriptions-item label="请求方法" :span="2">
          {{ currentLog.method }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentLog.params" label="请求参数" :span="2">
          <el-scrollbar max-height="120px">
            <pre class="params-content">{{ currentLog.params }}</pre>
          </el-scrollbar>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentLog.errorMsg" label="错误信息" :span="2">
          <span class="error-text">{{ currentLog.errorMsg }}</span>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  .title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }
}

.params-content {
  margin: 0;
  font-size: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-all;
}

.error-text {
  color: #f56c6c;
}
</style>
