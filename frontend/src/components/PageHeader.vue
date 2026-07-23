<script setup>
defineProps({
  title: {
    type: String,
    default: ''
  },
  subtitle: {
    type: String,
    default: ''
  }
})
</script>

<template>
  <div class="page-header">
    <div class="header-content">
      <div class="modern-badge mb-3" v-if="subtitle">
        <div class="dot pulse"></div>
        <span>{{ subtitle }}</span>
      </div>
      <h2 class="title font-display">{{ title }}</h2>
    </div>
    <div class="header-actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<style scoped lang="scss">
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #F1F5F9;

  .header-content {
    .title {
      margin: 0;
      font-size: 36px;
      font-weight: 400;
      color: #0F172A;
      letter-spacing: -0.02em;
    }
  }

  .header-actions {
    display: flex;
    gap: 12px;
  }
}
</style>
