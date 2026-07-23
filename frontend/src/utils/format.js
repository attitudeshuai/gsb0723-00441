import dayjs from 'dayjs'

export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return ''
  return dayjs(date).format(format)
}

export function formatDateTime(date) {
  return formatDate(date, 'YYYY-MM-DD HH:mm:ss')
}

export function formatGender(gender) {
  const map = { 1: '男', 2: '女' }
  return map[gender] || '-'
}

export function formatStudentStatus(status) {
  const map = { 1: '在读', 2: '转出', 3: '毕业' }
  return map[status] || '-'
}

export function formatTeacherStatus(status) {
  const map = { 1: '在职', 0: '离职' }
  return map[status] || '-'
}

export function formatAttendanceStatus(status) {
  const map = { 1: '出勤', 2: '迟到', 3: '早退', 4: '请假', 5: '旷课' }
  return map[status] || '-'
}

export function formatExamType(type) {
  const map = { 1: '期中考试', 2: '期末考试', 3: '单元测试', 4: '月考' }
  return map[type] || '-'
}

export function formatNoticeType(type) {
  const map = { 1: '通知', 2: '公告', 3: '紧急' }
  return map[type] || '-'
}

export function formatGrade(grade) {
  const map = {
    1: '一年级',
    2: '二年级',
    3: '三年级',
    4: '四年级',
    5: '五年级',
    6: '六年级'
  }
  return map[grade] || '-'
}

export function getGradeOptions() {
  return [
    { value: 1, label: '一年级' },
    { value: 2, label: '二年级' },
    { value: 3, label: '三年级' },
    { value: 4, label: '四年级' },
    { value: 5, label: '五年级' },
    { value: 6, label: '六年级' }
  ]
}

export function getGenderOptions() {
  return [
    { value: 1, label: '男' },
    { value: 2, label: '女' }
  ]
}

export function getStudentStatusOptions() {
  return [
    { value: 1, label: '在读' },
    { value: 2, label: '转出' },
    { value: 3, label: '毕业' }
  ]
}

export function getAttendanceStatusOptions() {
  return [
    { value: 1, label: '出勤' },
    { value: 2, label: '迟到' },
    { value: 3, label: '早退' },
    { value: 4, label: '请假' },
    { value: 5, label: '旷课' }
  ]
}

export function getExamTypeOptions() {
  return [
    { value: 1, label: '期中考试' },
    { value: 2, label: '期末考试' },
    { value: 3, label: '单元测试' },
    { value: 4, label: '月考' }
  ]
}

export function getNoticeTypeOptions() {
  return [
    { value: 1, label: '通知' },
    { value: 2, label: '公告' },
    { value: 3, label: '紧急' }
  ]
}
