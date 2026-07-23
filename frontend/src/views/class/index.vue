<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getClassList, addClass, updateClass, deleteClass, getClassStudents } from '@/api/class'
import { getAllTeachers } from '@/api/teacher'
import { formatGrade, getGradeOptions } from '@/utils/format'

const loading = ref(false)
const classList = ref([])
const teacherList = ref([])
const currentGrade = ref('')

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const formData = reactive({
  id: null,
  grade: '',
  classNo: '',
  className: '',
  teacherId: ''
})

const rules = {
  grade: [{ required: true, message: '请选择年级', trigger: 'change' }],
  classNo: [{ required: true, message: '请输入班号', trigger: 'blur' }],
  className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }]
}

const filteredClasses = computed(() => {
  if (!currentGrade.value) return classList.value
  return classList.value.filter(c => c.grade === currentGrade.value)
})

const groupedClasses = computed(() => {
  const groups = {}
  filteredClasses.value.forEach(item => {
    const grade = item.grade
    if (!groups[grade]) {
      groups[grade] = []
    }
    groups[grade].push(item)
  })
  return groups
})

onMounted(async () => {
  await Promise.all([loadData(), loadTeachers()])
})

async function loadData() {
  loading.value = true
  try {
    classList.value = await getClassList()
  } catch {
    classList.value = []
  } finally {
    loading.value = false
  }
}

async function loadTeachers() {
  try {
    teacherList.value = await getAllTeachers()
  } catch {
    teacherList.value = []
  }
}

function handleAdd() {
  dialogTitle.value = '新增班级'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑班级'
  Object.assign(formData, {
    id: row.id,
    grade: row.grade,
    classNo: row.classNo,
    className: row.className,
    teacherId: row.teacherId
  })
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除"${row.className}"吗？`, '提示', { type: 'warning' })
    await deleteClass(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 取消
  }
}

function resetForm() {
  formData.id = null
  formData.grade = ''
  formData.classNo = ''
  formData.className = ''
  formData.teacherId = ''
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    if (formData.id) {
      await updateClass(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await addClass(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {
    // 错误已处理
  }
}

function updateClassName() {
  if (formData.grade && formData.classNo) {
    formData.className = `${formatGrade(formData.grade)}${formData.classNo}班`
  }
}

// 查看班级学生
const studentsVisible = ref(false)
const studentsLoading = ref(false)
const currentClass = ref({})
const studentList = ref([])

async function handleViewStudents(item) {
  currentClass.value = item
  studentsVisible.value = true
  studentsLoading.value = true
  try {
    studentList.value = await getClassStudents(item.id)
  } catch {
    studentList.value = []
  } finally {
    studentsLoading.value = false
  }
}
</script>

<template>
  <div class="page-container">
    <div class="search-area">
      <el-form inline>
        <el-form-item label="年级">
          <el-select v-model="currentGrade" placeholder="全部年级" clearable style="width: 140px">
            <el-option v-for="item in getGradeOptions()" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增班级
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div v-loading="loading" class="class-list-container">
      <template v-for="(classes, grade) in groupedClasses" :key="grade">
        <div class="grade-section">
          <div class="grade-header">
            <h3 class="grade-title">{{ formatGrade(Number(grade)) }}</h3>
            <span class="grade-count">{{ classes.length }} 个班级</span>
          </div>
          <el-row :gutter="24">
            <el-col v-for="item in classes" :key="item.id" :xs="24" :sm="12" :md="8" :lg="6">
              <div class="class-bento-card">
                <div class="card-top">
                  <div class="class-badge">
                    <span class="dot pulse"></span>
                    {{ item.className }}
                  </div>
                  <el-dropdown trigger="click">
                    <el-icon class="more-btn"><MoreFilled /></el-icon>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item @click="handleViewStudents(item)">
                          <el-icon><User /></el-icon>查看学生
                        </el-dropdown-item>
                        <el-dropdown-item @click="handleEdit(item)">
                          <el-icon><Edit /></el-icon>编辑班级
                        </el-dropdown-item>
                        <el-dropdown-item @click="handleDelete(item)" divided class="text-red-500">
                          <el-icon><Delete /></el-icon>删除
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>

                <div class="card-body">
                  <div class="info-item">
                    <div class="icon-box bg-blue-50 text-blue-500"><el-icon><Avatar /></el-icon></div>
                    <div class="info-content">
                      <span class="label">班主任</span>
                      <span class="value">{{ item.teacherName || '未分配' }}</span>
                    </div>
                  </div>
                  <div class="info-item">
                    <div class="icon-box bg-emerald-50 text-emerald-500"><el-icon><UserFilled /></el-icon></div>
                    <div class="info-content">
                      <span class="label">学生总数</span>
                      <span class="value">{{ item.studentCount || 0 }}<small>人</small></span>
                    </div>
                  </div>
                </div>

                <div class="card-footer">
                  <el-button type="primary" link @click="handleViewStudents(item)">管理学生</el-button>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </template>
      <el-empty v-if="!filteredClasses.length" description="暂无班级数据" />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close append-to-body>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="年级" prop="grade">
          <el-select v-model="formData.grade" placeholder="请选择年级" style="width: 100%" @change="updateClassName">
            <el-option v-for="item in getGradeOptions()" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="班号" prop="classNo">
          <el-input-number v-model="formData.classNo" :min="1" :max="10" @change="updateClassName" />
        </el-form-item>
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="formData.className" placeholder="班级名称" />
        </el-form-item>
        <el-form-item label="班主任" prop="teacherId">
          <el-select v-model="formData.teacherId" placeholder="请选择班主任" clearable style="width: 100%">
            <el-option v-for="item in teacherList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 班级学生列表对话框 -->
    <el-dialog v-model="studentsVisible" :title="`${currentClass.className} - 学生名单（${studentList.length}人）`" width="700px" destroy-on-close append-to-body>
      <el-table v-loading="studentsLoading" :data="studentList" border stripe max-height="400">
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="{ row }">{{ row.gender === 1 ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="parentName" label="家长姓名" width="100" />
        <el-table-column prop="parentPhone" label="家长电话" min-width="130" />
      </el-table>
      <el-empty v-if="!studentsLoading && !studentList.length" description="暂无学生" />
      <template #footer>
        <el-button @click="studentsVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.class-list-container {
  min-height: 400px;
}

.grade-section {
  margin-bottom: 40px;
  &:last-child { margin-bottom: 0; }
}

.grade-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 20px;
  padding-left: 4px;

  .grade-title {
    font-size: 20px;
    font-weight: 800;
    color: #1E293B;
    letter-spacing: -0.5px;
  }

  .grade-count {
    font-size: 13px;
    font-weight: 600;
    color: #94A3B8;
  }
}

.class-bento-card {
  background: #fff;
  border-radius: 24px;
  border: 1px solid #F1F5F9;
  padding: 24px;
  margin-bottom: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0; left: 0; right: 0; height: 4px;
    background: var(--signature-gradient);
    opacity: 0;
    transition: opacity 0.3s;
  }

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.05), 0 10px 10px -5px rgba(0, 0, 0, 0.02);
    border-color: #E2E8F0;
    &::before { opacity: 1; }
    .more-btn { opacity: 1; }
  }

  .card-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .class-badge {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 6px 14px;
      background: #F8FAFC;
      border-radius: 12px;
      font-weight: 700;
      color: #1E293B;
      font-size: 15px;

      .dot {
        width: 8px; height: 8px; border-radius: 50%;
        background: #3B82F6;
        &.pulse { animation: pulse 2s infinite; }
      }
    }

    .more-btn {
      cursor: pointer;
      color: #94A3B8;
      padding: 8px;
      border-radius: 10px;
      transition: all 0.2s;
      opacity: 0.6;
      &:hover {
        background: #F1F5F9;
        color: #1E293B;
      }
    }
  }

  .card-body {
    display: flex;
    flex-direction: column;
    gap: 16px;
    margin-bottom: 24px;

    .info-item {
      display: flex;
      align-items: center;
      gap: 12px;

      .icon-box {
        width: 40px; height: 40px; border-radius: 12px;
        display: flex; align-items: center; justify-content: center;
        font-size: 18px;
      }

      .info-content {
        display: flex;
        flex-direction: column;
        .label { font-size: 11px; font-weight: 600; color: #94A3B8; text-transform: uppercase; }
        .value { font-size: 15px; font-weight: 700; color: #334155; }
        small { font-size: 12px; font-weight: 600; margin-left: 2px; }
      }
    }
  }

  .card-footer {
    padding-top: 16px;
    border-top: 1px solid #F1F5F9;
    display: flex;
    justify-content: flex-end;
  }
}

@keyframes pulse {
  0% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(59, 130, 246, 0.7); }
  70% { transform: scale(1); box-shadow: 0 0 0 6px rgba(59, 130, 246, 0); }
  100% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(59, 130, 246, 0); }
}
</style>
