import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, clearAuth } from './auth'
import router from '@/router'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 错误消息映射 - 用户友好的错误提示
const errorMessages = {
  400: '请求参数有误，请检查后重试',
  401: '登录已过期，请重新登录',
  403: '没有权限访问该资源',
  404: '请求的资源不存在',
  405: '请求方式不正确，请联系管理员',
  408: '请求超时，请稍后重试',
  500: '服务器开小差了，请稍后再试',
  502: '网关错误，请稍后重试',
  503: '服务暂时不可用，请稍后重试',
  504: '网关超时，请稍后重试'
}

request.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

request.interceptors.response.use(
  response => {
    // 处理blob响应类型（如导出文件）
    if (response.config.responseType === 'blob') {
      return response.data
    }

    const { code, message, data } = response.data

    if (code === 200) {
      return data
    }

    ElMessage.error(message || '请求失败')
    return Promise.reject(new Error(message))
  },
  error => {
    if (error.response) {
      const { status, data } = error.response

      // 获取用户友好的错误消息，优先使用后端返回的详细错误信息
      let errorMessage = data?.message || errorMessages[status] || '请求失败，请稍后重试'

      // 处理特殊状态码
      switch (status) {
        case 400:
          ElMessage.error(errorMessage)
          break
        case 401:
          ElMessage.error(errorMessage)
          clearAuth()
          router.push('/login')
          break
        case 403:
          ElMessage.error(errorMessage)
          break
        case 404:
        case 405:
        case 500:
        case 502:
        case 503:
        case 504:
          ElMessage.error(errorMessage)
          break
        default:
          ElMessage.error(errorMessage)
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请检查网络后重试')
    } else if (error.message?.includes('Network Error')) {
      ElMessage.error('网络连接失败，请检查网络设置')
    } else {
      ElMessage.error('请求失败，请稍后重试')
    }

    return Promise.reject(error)
  }
)

export default request
