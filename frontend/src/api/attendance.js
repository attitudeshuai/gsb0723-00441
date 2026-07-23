import request from '@/utils/request'

export function getAttendanceList(params) {
  return request.get('/attendances', { params })
}

export function inputAttendances(data) {
  return request.post('/attendances', data)
}

export function updateAttendance(id, data) {
  return request.put(`/attendances/${id}`, data)
}

export function getAttendanceStats(params) {
  return request.get('/attendances/stats', { params })
}
