import request from '@/utils/request'

export function getRoleList(params) {
  return request.get('/roles', { params })
}

export function getRole(id) {
  return request.get(`/roles/${id}`)
}

export function addRole(data) {
  return request.post('/roles', data)
}

export function updateRole(id, data) {
  return request.put(`/roles/${id}`, data)
}

export function deleteRole(id) {
  return request.delete(`/roles/${id}`)
}

export function getAllRoles() {
  return request.get('/roles')
}
