import request from '@/utils/request'

export function getUserList(params) {
  return request.get('/users', { params })
}

export function getUser(id) {
  return request.get(`/users/${id}`)
}

export function addUser(data) {
  return request.post('/users', data)
}

export function updateUser(id, data) {
  return request.put(`/users/${id}`, data)
}

export function deleteUser(id) {
  return request.delete(`/users/${id}`)
}

export function resetPassword(id) {
  return request.put(`/users/${id}/reset-password`)
}

export function toggleUserStatus(id) {
  return request.put(`/users/${id}/toggle-status`)
}
