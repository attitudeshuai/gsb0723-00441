import { defineStore } from 'pinia'
import { login as loginApi, getUserInfo, logout as logoutApi } from '@/api/auth'
import { getToken, setToken, getUser, setUser, clearAuth } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => {
    const savedUser = getUser() || {}
    return {
      token: getToken() || '',
      userInfo: savedUser,
      permissions: savedUser.permissions || []
    }
  },

  getters: {
    isLoggedIn: state => !!state.token,
    username: state => state.userInfo?.username || '',
    realName: state => state.userInfo?.realName || '',
    roleName: state => state.userInfo?.roleName || ''
  },

  actions: {
    async login(loginForm) {
      const data = await loginApi(loginForm)
      this.token = data.token
      this.userInfo = data.userInfo
      this.permissions = data.userInfo?.permissions || []
      setToken(data.token)
      setUser(data.userInfo)
      return data
    },

    async fetchUserInfo() {
      const userInfo = await getUserInfo()
      this.userInfo = userInfo
      this.permissions = userInfo?.permissions || []
      setUser(userInfo)
      return userInfo
    },

    async logout() {
      try {
        await logoutApi()
      } finally {
        this.resetState()
      }
    },

    resetState() {
      this.token = ''
      this.userInfo = {}
      this.permissions = []
      clearAuth()
    },

    hasPermission(permission) {
      if (this.permissions.includes('*')) {
        return true
      }
      return this.permissions.includes(permission)
    }
  }
})
