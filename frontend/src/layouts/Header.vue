<script setup>
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { Fold, Expand, ArrowDown, User, SwitchButton } from '@element-plus/icons-vue'
import Breadcrumb from './Breadcrumb.vue'

const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

function toggleSidebar() {
  appStore.toggleSidebar()
}

function handleCommand(command) {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      handleLogout()
      break
  }
}

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await userStore.logout()
    ElMessage.success('退出成功')
    router.push('/login')
  } catch {
    // 用户取消
  }
}
</script>

<template>
  <div class="header">
    <div class="header-left">
      <el-icon
        class="collapse-btn"
        :size="32"
        @click="toggleSidebar"
      >
        <Fold v-if="!appStore.sidebarCollapsed" />
        <Expand v-else />
      </el-icon>
      <div class="modern-badge ml-4">
       <div class="dot pulse"></div>
        <span>校园系统</span>
      </div>
      <Breadcrumb class="ml-6" />
    </div>

    <div class="header-right">
      <el-dropdown @command="handleCommand" trigger="click">
        <div class="user-info">
          <el-avatar :size="32" class="user-avatar" icon="UserFilled" />
          <span class="username">{{ userStore.realName || userStore.username }}</span>
          <el-icon class="ml-1"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu class="user-dropdown">
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>
              个人中心
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use "@/assets/styles/variables.scss" as *;

.header {
  width: 100%;
  height: 100%;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: transparent;
  border-bottom: none;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  color: $text-regular;
  transition: all 0.3s;

  &:hover {
    background-color: $bg-light;
    color: $primary-color;
  }
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 8px;
  transition: all 0.3s;

  &:hover {
    background-color: $bg-light;
  }

  .user-avatar {
    background-color: $primary-color;
  }

  .username {
    font-size: 14px;
    font-weight: 500;
    color: $text-primary;
  }

  .el-icon {
    font-size: 12px;
    color: $text-secondary;
  }
}

:global(.user-dropdown) {
  padding: 6px 0;
}
</style>
