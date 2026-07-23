import request from '@/utils/request'

export function getNoticeList(params) {
  return request.get('/notices', { params })
}

export function getNotice(id) {
  return request.get(`/notices/${id}`)
}

export function addNotice(data) {
  return request.post('/notices', data)
}

export function updateNotice(id, data) {
  return request.put(`/notices/${id}`, data)
}

export function deleteNotice(id) {
  return request.delete(`/notices/${id}`)
}

export function toggleNoticeTop(id) {
  return request.put(`/notices/${id}/top`)
}
