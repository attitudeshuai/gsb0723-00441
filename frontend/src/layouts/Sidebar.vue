<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { menuRoutes } from '@/router/routes'
import { hasPermission } from '@/utils/permission'

defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => route.path)

const filteredMenus = computed(() => {
  return filterMenusByPermission(menuRoutes)
})

function filterMenusByPermission(menus) {
  return menus
    .filter(menu => {
      const permission = menu.meta?.permission
      if (!permission) return true
      return hasPermission(permission)
    })
    .map(menu => {
      // 创建菜单副本，避免修改原始数据
      const menuCopy = { ...menu, meta: { ...menu.meta } }
      if (menu.children && menu.children.length) {
        menuCopy.children = filterMenusByPermission(menu.children)
      }
      return menuCopy
    })
    .filter(menu => {
      // 如果是父菜单且子菜单全部被过滤，则隐藏父菜单
      if (menu.children && menu.children.length === 0) {
        return false
      }
      return true
    })
}

function handleMenuSelect(path) {
  router.push(path)
}
</script>

<template>
  <div class="sidebar">
    <div class="logo">
      <div class="logo-icon">
        <el-icon :size="20"><School /></el-icon>
      </div>
      <span v-if="!collapsed" class="logo-text font-display gradient-text">赵村小学</span>
    </div>

    <div class="dot-pattern absolute inset-0 pointer-events-none"></div>

    <el-menu
      :default-active="activeMenu"
      :collapse="collapsed"
      :collapse-transition="false"
      background-color="transparent"
      text-color="#94A3B8"
      active-text-color="#FFFFFF"
      unique-opened
      router
      class="sidebar-refined"
      @select="handleMenuSelect"
    >
      <template v-for="menu in filteredMenus" :key="menu.path">
        <el-sub-menu v-if="menu.children && menu.children.length" :index="menu.path">
          <template #title>
            <el-icon><component :is="menu.meta.icon" /></el-icon>
            <span>{{ menu.meta.title }}</span>
          </template>
          <el-menu-item
            v-for="child in menu.children"
            :key="child.path"
            :index="child.path"
          >
            <el-icon><component :is="child.meta.icon" /></el-icon>
            <span>{{ child.meta.title }}</span>
          </el-menu-item>
        </el-sub-menu>

        <el-menu-item v-else :index="menu.path">
          <el-icon><component :is="menu.meta.icon" /></el-icon>
          <span>{{ menu.meta.title }}</span>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<style scoped lang="scss">
.sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #0F172A; // Slate 900
  border-right: 1px solid rgba(255, 255, 255, 0.05);
  position: relative;
}

.logo {
  height: 72px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 12px;
  color: #fff;
  z-index: 1;

  .logo-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    background: var(--signature-gradient);
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(30, 64, 175, 0.35);
  }

  .logo-text {
    font-size: 18px;
    font-weight: 400;
    white-space: nowrap;
    letter-spacing: -0.02em;
  }
}

.el-menu {
  border-right: none;
  flex: 1;
  overflow-y: auto;
  padding: 8px;
  z-index: 1;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 48px;
  line-height: 48px;
  margin: 4px 0;
  border-radius: 12px;
  transition: all 0.3s ease;

  &:hover {
    background-color: rgba(255, 255, 255, 0.05) !important;
    color: #fff !important;
    transform: translateX(4px);
  }

  .el-icon {
    font-size: 18px;
    margin-right: 12px;
    transition: transform 0.3s ease;
  }
}

:deep(.el-menu-item.is-active) {
  background-color: rgba(30, 64, 175, 0.12) !important;
  color: #fff !important;
  font-weight: 500;
  border-left: 3px solid var(--accent);
  transform: none;
  box-shadow: none;

  .el-icon {
    transform: none;
    color: var(--accent);
  }
}

:deep(.el-sub-menu .el-menu-item) {
  min-width: unset;
  margin: 4px 8px;
}
</style>
