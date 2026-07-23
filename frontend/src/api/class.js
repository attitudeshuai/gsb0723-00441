import request from '@/utils/request'

export function getClassList(params) {
  return request.get('/classes', { params })
}

export function getClass(id) {
  return request.get(`/classes/${id}`)
}

export function addClass(data) {
  return request.post('/classes', data)
}

export function updateClass(id, data) {
  return request.put(`/classes/${id}`, data)
}

export function deleteClass(id) {
  return request.delete(`/classes/${id}`)
}

export function getClassStudents(id) {
  return request.get(`/classes/${id}/students`)
}

export function getAllClasses() {
  return request.get('/classes')
}
