export const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'student',
        name: 'Student',
        component: () => import('@/views/student/index.vue'),
        meta: { title: '学生管理', icon: 'User', permission: 'student:list' }
      },
      {
        path: 'student/:id',
        name: 'StudentDetail',
        component: () => import('@/views/student/detail.vue'),
        meta: { title: '学生详情', hidden: true, permission: 'student:list' }
      },
      {
        path: 'teacher',
        name: 'Teacher',
        component: () => import('@/views/teacher/index.vue'),
        meta: { title: '教师管理', icon: 'UserFilled', permission: 'teacher:list' }
      },
      {
        path: 'class',
        name: 'Class',
        component: () => import('@/views/class/index.vue'),
        meta: { title: '班级管理', icon: 'School', permission: 'class:list' }
      },
      {
        path: 'course',
        name: 'Course',
        component: () => import('@/views/course/index.vue'),
        meta: { title: '课程管理', icon: 'Reading', permission: 'course:list' }
      },
      {
        path: 'exam',
        name: 'Exam',
        component: () => import('@/views/exam/index.vue'),
        meta: { title: '考试管理', icon: 'Tickets', permission: 'score:list' }
      },
      {
        path: 'score',
        name: 'Score',
        component: () => import('@/views/score/index.vue'),
        meta: { title: '成绩管理', icon: 'Document', permission: 'score:list' }
      },
      {
        path: 'score/stats',
        name: 'ScoreStats',
        component: () => import('@/views/score/stats.vue'),
        meta: { title: '成绩统计', hidden: true, permission: 'score:list' }
      },
      {
        path: 'attendance',
        name: 'Attendance',
        component: () => import('@/views/attendance/index.vue'),
        meta: { title: '考勤管理', icon: 'Calendar', permission: 'attendance:list' }
      },
      {
        path: 'attendance/stats',
        name: 'AttendanceStats',
        component: () => import('@/views/attendance/stats.vue'),
        meta: { title: '考勤统计', hidden: true, permission: 'attendance:list' }
      },
      {
        path: 'notice',
        name: 'Notice',
        component: () => import('@/views/notice/index.vue'),
        meta: { title: '通知公告', icon: 'Bell', permission: 'notice:list' }
      },
      {
        path: 'system/user',
        name: 'SystemUser',
        component: () => import('@/views/system/user.vue'),
        meta: { title: '用户管理', icon: 'Avatar', permission: 'user:list', parent: 'system' }
      },
      {
        path: 'system/role',
        name: 'SystemRole',
        component: () => import('@/views/system/role.vue'),
        meta: { title: '角色管理', icon: 'Key', permission: 'role:list', parent: 'system' }
      },
      {
        path: 'system/log',
        name: 'SystemLog',
        component: () => import('@/views/system/log.vue'),
        meta: { title: '操作日志', icon: 'Document', permission: 'log:list', parent: 'system' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', icon: 'User', hidden: true }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404', requiresAuth: false }
  }
]

export const menuRoutes = [
  {
    path: '/dashboard',
    name: 'Dashboard',
    meta: { title: '首页', icon: 'HomeFilled' }
  },
  {
    path: '/student',
    name: 'Student',
    meta: { title: '学生管理', icon: 'User', permission: 'student:list' }
  },
  {
    path: '/teacher',
    name: 'Teacher',
    meta: { title: '教师管理', icon: 'UserFilled', permission: 'teacher:list' }
  },
  {
    path: '/class',
    name: 'Class',
    meta: { title: '班级管理', icon: 'School', permission: 'class:list' }
  },
  {
    path: '/course',
    name: 'Course',
    meta: { title: '课程管理', icon: 'Reading', permission: 'course:list' }
  },
  {
    path: '/exam',
    name: 'Exam',
    meta: { title: '考试管理', icon: 'Tickets', permission: 'score:list' }
  },
  {
    path: '/score',
    name: 'Score',
    meta: { title: '成绩管理', icon: 'Document', permission: 'score:list' }
  },
  {
    path: '/attendance',
    name: 'Attendance',
    meta: { title: '考勤管理', icon: 'Calendar', permission: 'attendance:list' }
  },
  {
    path: '/notice',
    name: 'Notice',
    meta: { title: '通知公告', icon: 'Bell', permission: 'notice:list' }
  },
  {
    path: '/system',
    name: 'System',
    meta: { title: '系统管理', icon: 'Setting', permission: 'user:list' },
    children: [
      {
        path: '/system/user',
        name: 'SystemUser',
        meta: { title: '用户管理', icon: 'Avatar', permission: 'user:list' }
      },
      {
        path: '/system/role',
        name: 'SystemRole',
        meta: { title: '角色管理', icon: 'Key', permission: 'role:list' }
      },
      {
        path: '/system/log',
        name: 'SystemLog',
        meta: { title: '操作日志', icon: 'Document', permission: 'log:list' }
      }
    ]
  }
]
