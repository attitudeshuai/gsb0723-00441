# 赵村中心小学管理系统

农村小学信息化管理系统，支持学生、教师、班级、成绩、考勤、通知公告等核心功能。

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2
- MyBatis-Plus 3.5
- MySQL 8.0
- JWT 认证

### 前端
- Vue 3.4
- Vite 5
- Element Plus
- Pinia
- ECharts

## 快速开始

### 环境要求
- Docker & Docker Compose

### Docker 部署

```bash
# 构建并启动所有服务
docker compose up -d --build
```

## 访问地址

- http://localhost:3000

## 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 超级管理员 |


## 功能模块

- 系统管理：用户管理、角色管理
- 学生管理：学生信息 CRUD、转班、导入导出
- 教师管理：教师信息 CRUD
- 班级管理：班级信息、学生名单
- 成绩管理：成绩录入、统计分析
- 考勤管理：考勤录入、统计报表
- 通知公告：发布、置顶、管理

