import { createRouter, createWebHistory } from 'vue-router'
import { routes } from './routes'
import { getToken, getUser } from '@/utils/auth'
import NProgress from 'nprogress'

const router = createRouter({
  history: createWebHistory(),
  routes
})

const whiteList = ['/login']

// 检查用户是否有权限访问路由
function checkPermission(permission, userPermissions) {
  if (!permission) return true
  if (!userPermissions || userPermissions.length === 0) return false
  if (userPermissions.includes('*')) return true

  // 检查模块级通配符权限
  if (permission.includes(':')) {
    const [module] = permission.split(':')
    if (userPermissions.includes(`${module}:*`)) return true
  }

  return userPermissions.includes(permission)
}

router.beforeEach((to, from, next) => {
  NProgress?.start?.()
  document.title = to.meta?.title ? `${to.meta.title} - 赵村中心小学` : '赵村中心小学管理系统'

  const token = getToken()
  const requiresAuth = to.meta?.requiresAuth !== false

  if (token) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      // 检查路由权限
      const permission = to.meta?.permission
      if (permission) {
        const user = getUser()
        const userPermissions = user?.permissions || []
        if (!checkPermission(permission, userPermissions)) {
          // 无权限，跳转到首页
          next({ path: '/dashboard' })
          return
        }
      }
      next()
    }
  } else {
    if (whiteList.includes(to.path) || !requiresAuth) {
      next()
    } else {
      next({ path: '/login', query: { redirect: to.fullPath } })
    }
  }
})

router.afterEach(() => {
  NProgress?.done?.()
})

export default router
