<script setup>
defineProps({
  title: {
    type: String,
    default: ''
  },
  value: {
    type: [String, Number],
    default: '-'
  },
  icon: {
    type: String,
    default: ''
  },
  color: {
    type: String,
    default: 'primary' // primary, success, warning, danger, info
  },
  trend: {
    type: Number,
    default: null
  }
})
</script>

<template>
  <div class="stat-card group">
    <div :class="['stat-icon-wrapper', `bg-${color}`]">
      <el-icon class="stat-icon" :size="22">
        <component :is="icon" />
      </el-icon>
    </div>
    <div class="stat-content">
      <div class="stat-value font-display">{{ value }}</div>
      <div class="stat-label">
        <span class="font-mono text-[10px] uppercase tracking-wider opacity-60">{{ title }}</span>
        <div v-if="trend !== null" :class="['stat-trend', trend >= 0 ? 'text-success' : 'text-danger']">
          <el-icon :size="12"><CaretTop v-if="trend >= 0" /><CaretBottom v-else /></el-icon>
          <span class="font-mono text-xs">{{ Math.abs(trend) }}%</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use "@/assets/styles/variables.scss" as *;

.stat-card {
  display: flex;
  align-items: center;
  padding: 24px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border-radius: 20px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.02), 0 2px 4px -1px rgba(0, 0, 0, 0.01);
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  border: 1px solid rgba(255, 255, 255, 0.5);

  &:hover {
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.05);
    transform: translateY(-4px);
    border-color: rgba($primary-accent, 0.2);
    background: #FFFFFF;
  }
}

.stat-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 14px;
  margin-right: 18px;
  flex-shrink: 0;
  color: white;
  transition: transform 0.4s ease;

  .group:hover & {
    transform: scale(1.1) rotate(-5deg);
  }

  /* Gradient Variants */
  &.bg-primary { background: var(--signature-gradient); box-shadow: 0 4px 12px rgba(0, 82, 255, 0.25); }
  &.bg-success { background: linear-gradient(135deg, #10B981, #34D399); box-shadow: 0 4px 12px rgba(16, 185, 129, 0.25); }
  &.bg-warning { background: linear-gradient(135deg, #F59E0B, #FBBF24); box-shadow: 0 4px 12px rgba(245, 158, 11, 0.25); }
  &.bg-danger { background: linear-gradient(135deg, #EF4444, #F87171); box-shadow: 0 4px 12px rgba(239, 68, 68, 0.25); }
  &.bg-info { background: linear-gradient(135deg, #64748B, #94A3B8); box-shadow: 0 4px 12px rgba(100, 116, 139, 0.25); }
}

.stat-value {
  font-size: 32px;
  color: #0F172A;
  line-height: 1;
  margin-bottom: 6px;
}

.stat-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stat-trend {
  display: inline-flex;
  align-items: center;
  padding: 2px 6px;
  border-radius: 6px;
  background: rgba(var(--el-color-success-rgb), 0.1);

  &.text-danger {
    background: rgba(var(--el-color-danger-rgb), 0.1);
  }
}
</style>
