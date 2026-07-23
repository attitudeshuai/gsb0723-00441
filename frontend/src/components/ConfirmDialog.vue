<script setup>
import { ElMessageBox } from 'element-plus'

const props = defineProps({
  title: {
    type: String,
    default: '确认操作'
  },
  content: {
    type: String,
    default: '确定要执行此操作吗？'
  },
  confirmText: {
    type: String,
    default: '确定'
  },
  cancelText: {
    type: String,
    default: '取消'
  },
  type: {
    type: String,
    default: 'warning'
  }
})

const emit = defineEmits(['confirm', 'cancel'])

async function showConfirm() {
  try {
    await ElMessageBox.confirm(props.content, props.title, {
      confirmButtonText: props.confirmText,
      cancelButtonText: props.cancelText,
      type: props.type
    })
    emit('confirm')
  } catch {
    emit('cancel')
  }
}

defineExpose({ showConfirm })
</script>

<template>
  <span @click="showConfirm">
    <slot />
  </span>
</template>
