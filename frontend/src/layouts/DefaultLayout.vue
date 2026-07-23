<script setup>
import { computed } from 'vue'
import { useAppStore } from '@/stores/app'
import Sidebar from './Sidebar.vue'
import AppHeader from './Header.vue'

const appStore = useAppStore()
const collapsed = computed(() => appStore.sidebarCollapsed)
</script>

<template>
  <el-container class="layout-container">
    <el-aside :width="collapsed ? '80px' : '240px'" class="layout-aside">
      <Sidebar :collapsed="collapsed" />
    </el-aside>
    <el-container class="main-container">
      <!-- Decorative Radial Glows -->
      <div class="radial-glow glow-1"></div>
      <div class="radial-glow glow-2"></div>

      <el-header class="layout-header">
        <AppHeader />
      </el-header>
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped lang="scss">
@use "@/assets/styles/variables.scss" as *;

.layout-container {
  height: 100vh;
  width: 100%;
}

.layout-aside {
  background-color: #0F172A; // Slate 900
  transition: width $transition-duration;
  overflow: hidden;
  box-shadow: 1px 0 0 0 $border-base;
  z-index: 10;
}

.layout-header {
  height: $header-height;
  padding: 0;
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(241, 245, 249, 0.8);
  z-index: 9;
}

.main-container {
  position: relative;
  overflow: hidden;
}

.radial-glow {
  position: absolute;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  filter: blur(100px);
  pointer-events: none;
  z-index: 0;
  opacity: 0.05;
}

.glow-1 {
  top: -100px;
  right: -100px;
  background: var(--accent);
}

.glow-2 {
  bottom: -100px;
  left: 100px;
  background: var(--accent-secondary);
}

.layout-main {
  background-color: transparent;
  overflow-y: auto;
  position: relative;
  z-index: 1;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
