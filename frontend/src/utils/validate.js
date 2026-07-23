// 手机号验证
export function isPhone(value) {
  return /^1[3-9]\d{9}$/.test(value)
}

// 用户名验证
export function isUsername(value) {
  return /^[a-zA-Z][a-zA-Z0-9_]{3,15}$/.test(value)
}

// 密码验证
export function isPassword(value) {
  return value && value.length >= 6
}

// 学号验证 (格式: 年份4位 + 年级1位 + 班级2位 + 序号2位，如: 202301001)
export function isStudentNo(value) {
  return /^\d{4}0[1-6]\d{4}$/.test(value)
}

// 教师工号验证 (格式: T + 6位数字，如: T100001)
export function isTeacherNo(value) {
  return /^T\d{6}$/.test(value)
}

// 身份证号验证 (18位)
export function isIdCard(value) {
  if (!value || value.length !== 18) return false
  const reg = /^[1-9]\d{5}(19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/
  return reg.test(value)
}

// 邮箱验证
export function isEmail(value) {
  return /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(value)
}

// 非空验证
export function isNotEmpty(value) {
  return value !== null && value !== undefined && value !== ''
}

// 正整数验证
export function isPositiveInteger(value) {
  return /^[1-9]\d*$/.test(value)
}

// 分数验证 (0-100)
export function isScore(value) {
  const num = Number(value)
  return !isNaN(num) && num >= 0 && num <= 100
}

// =====================================
// Element Plus 表单验证器
// =====================================

export const phoneValidator = (rule, value, callback) => {
  if (!value) {
    callback()
    return
  }
  if (isPhone(value)) {
    callback()
  } else {
    callback(new Error('请输入正确的手机号'))
  }
}

export const phoneRequiredValidator = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号'))
    return
  }
  if (isPhone(value)) {
    callback()
  } else {
    callback(new Error('请输入正确的手机号'))
  }
}

export const usernameValidator = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入用户名'))
    return
  }
  if (isUsername(value)) {
    callback()
  } else {
    callback(new Error('用户名需4-16位，字母开头，可包含字母数字下划线'))
  }
}

export const passwordValidator = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
    return
  }
  if (isPassword(value)) {
    callback()
  } else {
    callback(new Error('密码长度不能少于6位'))
  }
}

export const studentNoValidator = (rule, value, callback) => {
  if (!value) {
    callback()
    return
  }
  if (isStudentNo(value)) {
    callback()
  } else {
    callback(new Error('学号格式不正确'))
  }
}

export const teacherNoValidator = (rule, value, callback) => {
  if (!value) {
    callback()
    return
  }
  if (isTeacherNo(value)) {
    callback()
  } else {
    callback(new Error('工号格式不正确（如：T100001）'))
  }
}

export const idCardValidator = (rule, value, callback) => {
  if (!value) {
    callback()
    return
  }
  if (isIdCard(value)) {
    callback()
  } else {
    callback(new Error('请输入正确的身份证号'))
  }
}

export const emailValidator = (rule, value, callback) => {
  if (!value) {
    callback()
    return
  }
  if (isEmail(value)) {
    callback()
  } else {
    callback(new Error('请输入正确的邮箱地址'))
  }
}

export const scoreValidator = (rule, value, callback) => {
  if (value === '' || value === null || value === undefined) {
    callback()
    return
  }
  if (isScore(value)) {
    callback()
  } else {
    callback(new Error('分数需在0-100之间'))
  }
}

// =====================================
// 常用表单验证规则
// =====================================

export const formRules = {
  // 必填文本
  required: (message = '此项为必填项') => ({
    required: true,
    message,
    trigger: 'blur'
  }),

  // 必选下拉
  requiredSelect: (message = '请选择') => ({
    required: true,
    message,
    trigger: 'change'
  }),

  // 手机号
  phone: [
    { validator: phoneValidator, trigger: 'blur' }
  ],

  // 手机号（必填）
  phoneRequired: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { validator: phoneValidator, trigger: 'blur' }
  ],

  // 用户名
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { validator: usernameValidator, trigger: 'blur' }
  ],

  // 密码
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { validator: passwordValidator, trigger: 'blur' }
  ],

  // 学号
  studentNo: [
    { validator: studentNoValidator, trigger: 'blur' }
  ],

  // 工号
  teacherNo: [
    { validator: teacherNoValidator, trigger: 'blur' }
  ],

  // 身份证
  idCard: [
    { validator: idCardValidator, trigger: 'blur' }
  ],

  // 邮箱
  email: [
    { validator: emailValidator, trigger: 'blur' }
  ],

  // 分数
  score: [
    { validator: scoreValidator, trigger: 'blur' }
  ]
}
