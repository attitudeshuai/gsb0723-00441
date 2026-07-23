import request from '@/utils/request'

// 获取操作日志列表
export function getLogList(params) {
  return request.get('/logs', { params })
}

// 获取日志详情
export function getLogDetail(id) {
  return request.get(`/logs/${id}`)
}

// 清理日志（保留指定天数）
export function clearLogs(days = 30) {
  return request.delete('/logs/clean', { params: { days } })
}

// 导出日志
export function exportLogs(params) {
  return request.get('/logs/export', { params, responseType: 'blob' })
}
