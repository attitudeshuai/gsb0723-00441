<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { User, Lock } from '@element-plus/icons-vue'

const REMEMBER_KEY = 'school_remember_login'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const formRef = ref()

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

onMounted(() => {
  loadRememberedLogin()
})

function loadRememberedLogin() {
  try {
    const saved = localStorage.getItem(REMEMBER_KEY)
    if (saved) {
      const data = JSON.parse(saved)
      loginForm.username = data.username || ''
      loginForm.password = data.password ? atob(data.password) : ''
      loginForm.remember = true
    }
  } catch {
    localStorage.removeItem(REMEMBER_KEY)
  }
}

function saveRememberedLogin() {
  if (loginForm.remember) {
    localStorage.setItem(REMEMBER_KEY, JSON.stringify({
      username: loginForm.username,
      password: btoa(loginForm.password)
    }))
  } else {
    localStorage.removeItem(REMEMBER_KEY)
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login({
      username: loginForm.username,
      password: loginForm.password
    })

    saveRememberedLogin()
    ElMessage.success('登录成功')

    const redirect = route.query.redirect
    router.push(redirect || '/')
  } catch (error) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="visual-side">
      <div class="brand-info">
        <div class="logo-box">
          <el-icon :size="40" color="#fff"><School /></el-icon>
        </div>
        <h1 class="brand-name">赵村中心小学</h1>
        <p class="brand-desc">赋能教育数字化，链接校园每一个角落</p>
      </div>
      <div class="visual-bg"></div>
      <div class="floating-elements">
        <div class="circle c1"></div>
        <div class="circle c2"></div>
      </div>
    </div>

    <div class="form-side">
      <div class="form-container animate-fade-in">
        <div class="form-header">
          <h2>欢迎回来</h2>
          <p>请登录您的管理账户</p>
        </div>

        <el-form
          ref="formRef"
          :model="loginForm"
          :rules="rules"
          class="modern-form"
          @keyup.enter="handleLogin"
        >
          <div class="input-group">
            <label>用户名</label>
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="输入您的账号"
                :prefix-icon="User"
              />
            </el-form-item>
          </div>

          <div class="input-group">
            <label>密码</label>
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="输入您的密码"
                show-password
                :prefix-icon="Lock"
              />
            </el-form-item>
          </div>

          <div class="form-options">
            <el-checkbox v-model="loginForm.remember">保持登录状态</el-checkbox>
            <el-link type="primary" :underline="false">忘记密码？</el-link>
          </div>

          <button
            type="button"
            class="submit-btn"
            :class="{ loading }"
            :disabled="loading"
            @click="handleLogin"
          >
            <span>{{ loading ? '正在验证...' : '进入平台' }}</span>
            <el-icon v-if="!loading"><ArrowRight /></el-icon>
          </button>
        </el-form>

        <div class="form-footer">
          <p>© 2026 赵村中心小学 · 智慧校园管理系统</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use "@/assets/styles/variables.scss" as *;

.login-page {
  display: flex;
  width: 100%;
  height: 100vh;
  background: #fff;
  overflow: hidden;
}

.visual-side {
  position: relative;
  flex: 1.2;
  background: #0F172A;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 80px;
  color: #fff;
  overflow: hidden;

  @include respond-to(md) {
    display: none;
  }

  .brand-info {
    position: relative;
    z-index: 10;
    max-width: 480px;

    .logo-box {
      width: 72px;
      height: 72px;
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(10px);
      border-radius: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 40px;
      border: 1px solid rgba(255, 255, 255, 0.2);
    }

    .brand-name {
      font-size: 48px;
      font-weight: 800;
      margin-bottom: 16px;
      letter-spacing: -1px;
    }

    .brand-desc {
      font-size: 18px;
      color: rgba(255, 255, 255, 0.6);
      line-height: 1.6;
    }
  }

  .visual-bg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: url('https://images.unsplash.com/photo-1523050854058-8df90110c9f1?q=80&w=2070&auto=format&fit=crop');
    background-size: cover;
    background-position: center;
    opacity: 0.3;
    mix-blend-mode: overlay;
  }

  .floating-elements {
    .circle {
      position: absolute;
      border-radius: 50%;
      background: linear-gradient(135deg, $primary-accent 0%, transparent 100%);
      filter: blur(60px);
      z-index: 1;

      &.c1 { width: 400px; height: 400px; top: -100px; right: -100px; opacity: 0.2; }
      &.c2 { width: 300px; height: 300px; bottom: -50px; left: 10%; opacity: 0.15; }
    }
  }
}

.form-side {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: #fdfdfd;

  .form-container {
    width: 100%;
    max-width: 420px;
  }

  .form-header {
    margin-bottom: 48px;

    h2 {
      font-size: 32px;
      font-weight: 700;
      color: #111827;
      margin-bottom: 8px;
    }

    p {
      color: #6B7280;
      font-size: 16px;
    }
  }
}

.modern-form {
  .input-group {
    margin-bottom: 24px;

    label {
      display: block;
      font-size: 14px;
      font-weight: 600;
      color: #374151;
      margin-bottom: 8px;
    }

    :deep(.el-input__wrapper) {
      background: #F3F4F6;
      box-shadow: none !important;
      border-radius: 12px;
      padding: 12px 16px;
      border: 2px solid transparent;
      transition: all 0.2s;

      &.is-focus {
        background: #fff;
        border-color: $primary-accent;
      }
    }
  }

  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 32px;
  }

  .submit-btn {
    width: 100%;
    height: 56px;
    background: #2563EB;
    color: #fff;
    border: none;
    border-radius: 14px;
    font-size: 16px;
    font-weight: 600;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);

    &:hover {
      background: #3B82F6;
      transform: translateY(-2px);
      box-shadow: 0 10px 20px rgba(37, 99, 235, 0.2);
    }

    &:active {
      background: #1D4ED8;
      transform: translateY(0);
    }

    &.loading {
      opacity: 0.7;
      cursor: not-allowed;
    }
  }
}

.form-footer {
  margin-top: 60px;
  text-align: center;
  color: #9CA3AF;
  font-size: 13px;
}

.animate-fade-in {
  animation: fadeIn 0.8s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
