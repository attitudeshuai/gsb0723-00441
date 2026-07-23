import { useUserStore } from '@/stores/user'

export function hasPermission(permission) {
  const userStore = useUserStore()
  const permissions = userStore.permissions || []

  if (permissions.includes('*')) {
    return true
  }

  if (permission.includes(':')) {
    const [module] = permission.split(':')
    if (permissions.includes(`${module}:*`)) {
      return true
    }
  }

  return permissions.includes(permission)
}

export function hasAnyPermission(permissionList) {
  return permissionList.some(p => hasPermission(p))
}

export function hasAllPermissions(permissionList) {
  return permissionList.every(p => hasPermission(p))
}
