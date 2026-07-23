import request from '@/utils/request'

export function getStudentList(params) {
  return request.get('/students', { params })
}

export function getStudent(id) {
  return request.get(`/students/${id}`)
}

export function addStudent(data) {
  return request.post('/students', data)
}

export function updateStudent(id, data) {
  return request.put(`/students/${id}`, data)
}

export function deleteStudent(id) {
  return request.delete(`/students/${id}`)
}

export function transferStudent(id, classId) {
  return request.put(`/students/${id}/transfer`, null, { params: { classId } })
}

export function importStudents(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/students/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function exportStudents(params) {
  return request.get('/students/export', {
    params,
    responseType: 'blob'
  })
}
