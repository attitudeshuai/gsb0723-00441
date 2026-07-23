<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({})
  },
  fields: {
    type: Array,
    default: () => []
  },
  labelWidth: {
    type: String,
    default: '80px'
  }
})

const emit = defineEmits(['update:modelValue', 'search', 'reset'])

const formData = ref({ ...props.modelValue })

watch(() => props.modelValue, (val) => {
  formData.value = { ...val }
}, { deep: true })

function handleSearch() {
  emit('update:modelValue', formData.value)
  emit('search')
}

function handleReset() {
  const resetData = {}
  props.fields.forEach(field => {
    resetData[field.prop] = field.defaultValue !== undefined ? field.defaultValue : ''
  })
  formData.value = resetData
  emit('update:modelValue', resetData)
  emit('reset')
}
</script>

<template>
  <div class="search-form">
    <el-form :model="formData" inline :label-width="labelWidth">
      <template v-for="field in fields" :key="field.prop">
        <el-form-item :label="field.label">
          <template v-if="field.type === 'select'">
            <el-select
              v-model="formData[field.prop]"
              :placeholder="field.placeholder || `请选择${field.label}`"
              clearable
              :style="{ width: field.width || '160px' }"
            >
              <el-option
                v-for="opt in field.options"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>
          </template>
          <template v-else-if="field.type === 'date'">
            <el-date-picker
              v-model="formData[field.prop]"
              type="date"
              :placeholder="field.placeholder || `选择${field.label}`"
              value-format="YYYY-MM-DD"
              :style="{ width: field.width || '160px' }"
            />
          </template>
          <template v-else>
            <el-input
              v-model="formData[field.prop]"
              :placeholder="field.placeholder || `请输入${field.label}`"
              clearable
              :style="{ width: field.width || '160px' }"
            />
          </template>
        </el-form-item>
      </template>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>重置
        </el-button>
        <slot name="extra" />
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped lang="scss">
.search-form {
  margin-bottom: 16px;
  padding: 16px 16px 0;
  background: #fff;
  border-radius: 4px;
}
</style>
