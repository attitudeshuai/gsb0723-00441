import { useUserStore } from '@/stores/user'

/**
 * 权限指令
 * 用法: v-permission="'user:add'" 或 v-permission="['user:add', 'user:edit']"
 */
export const permission = {
  mounted(el, binding) {
    const { value } = binding
    const userStore = useUserStore()
    const permissions = userStore.permissions || []

    // 超级管理员拥有所有权限
    if (permissions.includes('*')) {
      return
    }

    let hasPermission = false

    if (value) {
      if (Array.isArray(value)) {
        // 数组：满足其中一个权限即可
        hasPermission = value.some(p => {
          if (permissions.includes(p)) return true
          // 检查通配符权限，如 user:* 可以匹配 user:add
          if (p.includes(':')) {
            const [module] = p.split(':')
            return permissions.includes(`${module}:*`)
          }
          return false
        })
      } else {
        // 字符串：检查单个权限
        hasPermission = permissions.includes(value)
        // 检查通配符权限
        if (!hasPermission && value.includes(':')) {
          const [module] = value.split(':')
          hasPermission = permissions.includes(`${module}:*`)
        }
      }
    }

    if (!hasPermission) {
      el.parentNode?.removeChild(el)
    }
  }
}

/**
 * 角色指令
 * 用法: v-role="'admin'" 或 v-role="['admin', 'principal']"
 */
export const role = {
  mounted(el, binding) {
    const { value } = binding
    const userStore = useUserStore()
    const userRole = userStore.userInfo?.roleCode

    let hasRole = false

    if (value) {
      if (Array.isArray(value)) {
        hasRole = value.includes(userRole)
      } else {
        hasRole = userRole === value
      }
    }

    if (!hasRole) {
      el.parentNode?.removeChild(el)
    }
  }
}

// 安装指令
export function setupPermissionDirectives(app) {
  app.directive('permission', permission)
  app.directive('role', role)
}

export default {
  install(app) {
    setupPermissionDirectives(app)
  }
}
