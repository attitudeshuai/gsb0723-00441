<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { changePassword } from '@/api/auth'

const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirm = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

async function handleChangePassword() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch {
    // 错误已处理
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page-container">
    <el-row :gutter="24">
      <el-col :span="10">
        <div class="content-card profile-info-card">
          <div class="profile-header">
            <div class="avatar-circle">
              {{ userStore.realName?.charAt(0) || 'U' }}
            </div>
            <h2 class="username">{{ userStore.realName }}</h2>
            <el-tag effect="light" round size="small">{{ userStore.roleName }}</el-tag>
          </div>

          <div class="info-list">
            <div class="info-item">
              <span class="label">账户名</span>
              <span class="value">{{ userStore.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">手机号码</span>
              <span class="value">{{ userStore.userInfo?.phone || '未绑定' }}</span>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :span="14">
        <div class="content-card">
          <div class="toolbar">
            <div class="title">安全设置</div>
          </div>

          <div class="security-section">
            <div class="section-head">
              <h3>修改登录密码</h3>
              <p>为了您的账号安全，请定期更换密码</p>
            </div>

            <el-form ref="formRef" :model="passwordForm" :rules="rules" label-position="top">
              <el-form-item label="原密码" prop="oldPassword">
                <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前使用的密码" show-password />
              </el-form-item>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="新密码" prop="newPassword">
                    <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="确认新密码" prop="confirmPassword">
                    <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
                  </el-form-item>
                </el-col>
              </el-row>
              <div class="form-actions mt-6">
                <el-button type="primary" :loading="loading" @click="handleChangePassword">确认更新密码</el-button>
              </div>
            </el-form>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped lang="scss">
:deep(.el-row) {
  display: flex;
  align-items: stretch;

  .el-col {
    display: flex;

    > .content-card {
      flex: 1;
    }
  }
}

.profile-info-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48px 24px;
}

.profile-header {
  text-align: center;
  margin-bottom: 40px;

  .avatar-circle {
    width: 80px; height: 80px;
    background: var(--signature-gradient);
    color: #fff;
    border-radius: 50%;
    display: flex; align-items: center; justify-content: center;
    font-size: 32px;
    font-weight: 800;
    margin: 0 auto 16px;
    box-shadow: 0 10px 15px -3px rgba(37, 99, 235, 0.3);
  }

  .username {
    font-size: 22px;
    font-weight: 800;
    color: #1E293B;
    margin-bottom: 8px;
  }
}

.info-list {
  width: 100%;
  .info-item {
    display: flex;
    justify-content: space-between;
    padding: 16px 0;
    border-bottom: 1px solid #F1F5F9;
    &:last-child { border-bottom: none; }
    .label { color: #94A3B8; font-size: 14px; font-weight: 500; }
    .value { color: #334155; font-size: 14px; font-weight: 700; }
  }
}

.security-section {
  .section-head {
    margin-bottom: 32px;
    h3 { font-size: 16px; font-weight: 700; color: #1E293B; margin-bottom: 4px; }
    p { font-size: 13px; color: #94A3B8; }
  }
}
</style>
