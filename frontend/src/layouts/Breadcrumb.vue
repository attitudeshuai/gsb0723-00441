<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { routes } from '@/router/routes'

const route = useRoute()

const breadcrumbs = computed(() => {
  const matched = route.matched
  const items = []

  matched.forEach(record => {
    if (record.meta?.title && record.path !== '/') {
      items.push({
        path: record.path,
        title: record.meta.title
      })
    }
  })

  if (route.meta?.parent === 'system') {
    items.unshift({
      path: '',
      title: '系统管理'
    })
  }

  return items
})
</script>

<template>
  <el-breadcrumb>
    <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item
      v-for="item in breadcrumbs"
      :key="item.path"
      :to="item.path ? { path: item.path } : undefined"
    >
      {{ item.title }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<style scoped lang="scss">
:deep(.el-breadcrumb__inner) {
  font-weight: normal;
}
</style>
